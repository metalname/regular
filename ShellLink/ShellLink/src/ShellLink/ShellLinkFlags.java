/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ShellLink;

/**
 *
 * @author metataro
 */
public enum ShellLinkFlags {
    
    HasLinkTargetIDList(1),
    HasLinkInfo(2),
    HasName(4),
    HasRelativePath(8),
    HasWorkingDir(16),
    HasArguments(32),
    HasIconLocation(64),
    IsUnicode(128),
    ForceNoLinkInfo(256),
    HasExpString(512),
    RunInSeparateProcess(1024),
    Unused1(2048),
    HasDarwinID(4096),
    RunAsUser(8192),
    HasExpIcon(16384),
    NoPidlAlias(32768),
    Unused2(65536),
    RunWithShimLayer(131072),
    ForceNoLinkTrack(262144),
    EnableTargetMetadata(524288),
    DisableLinkPathTracking(1048576),
    DisableKnownFolderTracking(2097152),
    DisableKnownFolderAlias(4194304),
    AllowLinkToLink(8388608),
    UnaliasOnSave(16777216),
    PreferEnvironmentPath(33554432),
    KeepLocalIDListForUNCTarget(67108864);
    
    public final int mask;

    private ShellLinkFlags(int mask) {
        this.mask = mask;
    }
    
    public int getMask() {
        return(mask);
    }
    
}
