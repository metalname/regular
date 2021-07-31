# Regular

Regular is a GUI offline Windows registry editor. This means it is launched from a USB drive on the machine containing the Windows OS that you want to edit.

Regular offers these features:

1) A GUI similar to RegEdit

2) Full access to modify any attributes of any key, including last-update timestamps and flags

3) Deletion of any key, or value within a key, including keys marked NODELETE, without creating log entries

4) Secure overwrite of deleted keys to ensure they cannot be recovered by forensic software, plus wipe of all previously deleted keys

# Installation

These instructions are for Linux. It is possible to run Regular from a Windows OS located on a USB drive, but that will not be covered here (nor is it recommended).

1) Create a bootable Linux USB. You can use any distribution - [Linux Lite](https://www.linuxliteos.com/) is recommended. Follow 
[these](https://www.linuxliteos.com/forums/tutorials/how-to-install-linux-lite-to-usb-stick-not-ready-yet!/) instructions to create a bootable Linux Lite USB.

**CAUTION**: DO NOT USE THE PC CONTAINING THE WINDOWS DISTRIBUTION YOU WISH TO EDIT TO DOWNLOAD OR CREATE THE USB IMAGE!!!

2) Boot from USB, and install Java if not already installed:

    sudo apt update
	
    sudo apt install default-jdk
    
3) Download the [latest release](https://github.com/metalname/regular/releases) of Regular from this repository. If you just want the binaries, look for the asset named `regular-x.x.x.zip` where x.x.x is the release tag

    
4) Unzip the file to any location on the Linux USB drive:

    unzip regular*.zip
    
5) Launch Regular:

    cd regular-x.x.x	# replace x.x.x with actual release number
	
	java -jar Regular.jar
	
# Mounting the Windows drive

**Important note** - if using Windows 8 or later, Linux will refuse to mount NTFS partitions that are hibernated. Since hibernation is turned on by default, you will need to turn off fast boot on Windows before shutting down. 
Follow instructions [here](https://www.windowscentral.com/how-disable-windows-10-fast-startup) to disable fast boot/hibernation.

Depending on the Linux distribution that the USB drive has, the NTFS partiton may be mounted automatically when Linux boots. This can be verified by running `lsblk -f` at the command line. Look for the largest NTFS partition - if it's 
mounted, the mount point will be listed next to the device name. If the NTFS partiton is not mounted, you can do so with these instructions:

1) Find the device name of the partition you want to mount. Type `lsblk -f` inthe terminal, and look for the largest NTFS partition. Make a note of the device name. It will be something like sdb4 (or nvme01p3 for PCI SSD drives)

2) Create a mount point for the NTFS partiton:

    sudo mkdir /mnt/windows
	
	sudo chmod 777 /mnt/windows
	
3) Mount the partition:

    sudo mount -t ntfs /dev/[device name] /mnt/windows 		# Replace [device name] with the actual device name of the NTFS partition
	
If you get a message that Linux is refusing to mount the partition due to hibernation, see note above to turn off Windows fast boot. Note that if you have turned off fast boot, Windows update has a habit of occasionally turning 
it back on, whether you want it or not.

4) Run Regular and navigate to the registry files at this path:

	/mnt/windows/Windows/System32/Config
	
Open one of the registry files (SYSTEM/SAM/SOFTWARE etc). (Note that there are other registry files located at different paths on the Windows filesystem - we're assuming that you know what you're doing).

# Backups

Each time a hive is opened, it will be backed up to $HOME/regBackup with the hive basename suffixed by a timestamp. Should something go wrong, the hive can be recovered from this location.

# Caution

This software is in a pre-release state. There is a non-zero chance that it could corrupt a registry file beyond any hope of recovery. It is strongly suggested that you make backups of the entire target filesystem before modification.
    
