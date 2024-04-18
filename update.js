if (!process.env.CI) globalThis.isMocha = true // gh-helpers
const fs = require('fs')
const cp = require('child_process')
const github = require('gh-helpers')()
const { join } = require('path')
const mcDecompiler = require('minecraft-java-decomp')
function exec (a, b) {
  console.log('$', [a, b])
  if (globalThis.isMocha && a.startsWith('git ')) return
  return cp.execSync(a, { ...b, stdio: 'inherit' })
}

function getCommitSHA (version) {
  const local = join(__dirname, '/.git/refs/heads/client' + version)
  const remote = join(__dirname, '/.git/refs/remotes/origin/client' + version)
  if (fs.existsSync(local)) {
    return fs.readFileSync(local, 'ascii').trim()
  } else if (fs.existsSync(remote)) {
    return fs.readFileSync(remote, 'ascii').trim()
  }
}

async function decomp (version) {
  exec('git pull')
  exec('ls -R .git/refs/')
  if (getCommitSHA(version)) {
    console.log('Already have decompiled', version, 'no work to do')
    return
  }
  await mcDecompiler.decompile(version, { path: join(__dirname, '/client_new'), force: true })
  console.log('Done decompiling!')
  exec('git checkout clientlatest')
  exec('mv client client_old')
  exec('mv client_new client')
  exec('git add client/*.java client/version.json')
  exec('git status')
  exec(`git commit -m "Add '${version}' sources"`)
  exec('git push origin clientlatest')
  exec(`git checkout -b client${version}`)
  exec(`git push origin client${version}`)
}

async function postprocess (repoData, version, oldVersion, isMajor, oldMajor) {
  if (isMajor) {
    console.log('Creating PR as major update from', oldMajor, version)
    const oldBranch = 'client' + oldMajor
    const newBranch = 'client' + version
    const pr = await github.createPullRequest(`MC ${oldMajor} -> ${version}`, `Minecraft Java Edition ${version}<br/>`, oldBranch, newBranch)
    console.log('Created PR', pr)
    github.sendWorkflowDispatch({
      owner: 'extremeheat',
      repo: 'llm-services',
      workflow: 'dispatch.yml',
      branch: 'main',
      inputs: {
        action: 'minecraft/pcReviewUpdateChanges',
        payload: JSON.stringify({
          repo: repoData,
          mode: 'release',
          oldVersion: oldMajor,
          newVersion: version,
          pr
        })
      }
    })
  } else {
    // A snapshot. Get the latest commit SHA on clientlatest and send it over to workflow dispatch so it will diff and make a comment for the commit to clientlatest
    const sha = getCommitSHA('latest')
    console.log('Snapshot update!', version, 'from', oldVersion, 'Commit SHA:', sha)
    github.sendWorkflowDispatch({
      owner: 'extremeheat',
      repo: 'llm-services',
      workflow: 'dispatch.yml',
      branch: 'main',
      inputs: {
        action: 'minecraft/pcReviewUpdateChanges',
        payload: JSON.stringify({
          repo: repoData,
          mode: 'snapshot',
          oldVersion: oldVersion,
          newVersion: version,
          commit: sha
        })
      }
    })
  }
}

async function main () {
  const repoDetails = await github.getRepoDetails()
  console.log('Repo details', repoDetails)
  const currentManifest = require('./version_manifest.json')
  const latestManifest = await fetch('https://launchermeta.mojang.com/mc/game/version_manifest.json').then(r => r.json())
  const latestRelease = latestManifest.versions.find(v => v.type === 'release')
  const latestSnapshot = latestManifest.versions.find(v => v.type === 'snapshot')

  if (currentManifest.latest.release === latestRelease.id && currentManifest.latest.snapshot === latestSnapshot.id) {
    console.log('No new version available')
  } else {
    // Store the old versions and check if this is a release update (if latest snap==latest rel). If release update we open PR otherwise we don't for snapshots
    const oldRel = currentManifest.latest.release
    const oldSnap = currentManifest.latest.snapshot
    const isRelease = currentManifest.latest.release === currentManifest.latest.snapshot
    console.log('New version available:', latestRelease.id, latestSnapshot.id, 'old:', oldSnap)
    fs.writeFileSync('version_manifest.json', JSON.stringify(latestManifest, null, 2))
    exec('git checkout updator')
    exec('git config user.name "github-actions[bot]"')
    exec('git config user.email "41898282+github-actions[bot]@users.noreply.github.com"')
    exec('git add version_manifest.json')
    exec(`git commit -m "Update version manifest for ${latestSnapshot.id}"`)
    exec('git push origin updator')
    await decomp(latestSnapshot.id)
    await postprocess(repoDetails, latestSnapshot.id, oldSnap, isRelease, oldRel)
  }
}

main()
