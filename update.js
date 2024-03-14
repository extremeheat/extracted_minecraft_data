const fs = require('fs')
const cp = require('child_process')
function exec(...a) {
  console.log('$', a)
  return cp.execSync(...a)
}

// function decomp () {
//   if (!fs.existsSync('DecompilerMC') || !fs.existsSync('DecompilerMC/.git')) {
//     fs.rmSync('DecompilerMC', { recursive: true, force: true })
//     exec('git clone http://github.com/extremeheat/DecompilermC --depth 1')
//   }
//   exec('cd DecompilerMC && git pull')
// }

async function main () {
  const currentManifest = require('./version_manifest.json')
  const latestManifest = await fetch('https://launchermeta.mojang.com/mc/game/version_manifest.json').then(r => r.json())
  const latestRelease = latestManifest.versions.find(v => v.type === 'release')
  const latestSnapshot = latestManifest.versions.find(v => v.type === 'snapshot')
  if (currentManifest.latest.release === latestRelease.id && currentManifest.latest.snapshot === latestSnapshot.id) {
    console.log('No new version available')
    return
  } else {
    console.log('New version available:', latestRelease.id, latestSnapshot.id)
    currentManifest.latest.release = latestRelease.id
    currentManifest.latest.snapshot = latestSnapshot.id
    fs.writeFileSync('version_manifest.json', JSON.stringify(currentManifest, null, 2))
    exec('git checkout updator')
    exec('git add version_manifest.json')
    exec('git commit -m "Update version manifest"')
    exec('git push origin updator')
  }
}

main()
