const fs = require('fs')
const cp = require('child_process')
function exec (a, b) {
  console.log('$', [a, b])
  return cp.execSync(a, { ...b, stdio: 'inherit' })
}

function decomp (version) {
  exec('git pull')
  if (!fs.existsSync('DecompilerMC') || !fs.existsSync('DecompilerMC/.git')) {
    fs.rmSync('DecompilerMC', { recursive: true, force: true })
    exec('git clone http://github.com/extremeheat/DecompilerMC --depth 1')
  }
  exec('cd DecompilerMC && git pull')
  exec(`cd DecompilerMC && python main.py -mcv ${version} -d fernflower`)
  console.log('Done decompiling!', fs.readdirSync('DecompilerMC/src'))
  exec('git checkout clientlatest')
  exec('mv client client_old')
  exec(`mv DecompilerMC/src/${version}/client ./client`)
  exec('git add client/*.java client/version.json')
  exec('git status')
  exec(`git commit -m "Add '${version}' sources"`)
  exec('git push origin clientlatest')
  exec(`git checkout -b client${version}`)
  exec(`git push origin client${version}`)
}

async function main () {
  const currentManifest = require('./version_manifest.json')
  const latestManifest = await fetch('https://launchermeta.mojang.com/mc/game/version_manifest.json').then(r => r.json())
  const latestRelease = latestManifest.versions.find(v => v.type === 'release')
  const latestSnapshot = latestManifest.versions.find(v => v.type === 'snapshot')

  decomp('24w11a')
  return

  if (currentManifest.latest.release === latestRelease.id && currentManifest.latest.snapshot === latestSnapshot.id) {
    console.log('No new version available')
    return
  } else {
    console.log('New version available:', latestRelease.id, latestSnapshot.id)
    currentManifest.latest.release = latestRelease.id
    currentManifest.latest.snapshot = latestSnapshot.id
    fs.writeFileSync('version_manifest.json', JSON.stringify(currentManifest, null, 2))
    exec('git checkout updator')
    exec('git config user.name "github-actions[bot]"')
    exec('git config user.email "41898282+github-actions[bot]@users.noreply.github.com"')
    exec('git add version_manifest.json')
    exec(`git commit -m "Update version manifest for ${latestSnapshot.id}"`)
    exec('git push origin updator')
  }
}

main()
