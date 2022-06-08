# Environmental Music

A Fabric mod for Minecraft that replaces the game's music system with an environment-based one.

## Usage

To install the mod, you must first install [Fabric](https://fabricmc.net/use/installer/). After installing Fabric, download [the mod](https://github.com/Kamppix/EnvironmentalMusic/releases/latest) and [Fabric API](https://github.com/FabricMC/fabric/releases). Place the downloaded JAR files in `.minecraft/mods` and the mod will be installed.

[Where is .minecraft?](https://minecraft.fandom.com/wiki/.minecraft#Locating_.minecraft)

### Music packs

In addition to installing the mod, you'll need to get yourself a resource pack that acts as a music pack for the mod.

Music packs are placed in the same directory as any other resource packs, `.minecraft/resourcepacks`.

You can create a music pack using [Pixel Music Packer](https://github.com/Kamppix/PixelMusicPacker) or, if you want, you can do it manually.

#### To create a music pack manually (why):

1. Download the music pack template provided with your version of the mod
2. Place the OGG music files in `assets/environmentalmusic/sounds` (filenames must consist of [a-z0-9/.\_-])
3. Insert the music filenames into `sounds.json` as follows: `"name": "environmentalmusic:[filename without .ogg]"`
