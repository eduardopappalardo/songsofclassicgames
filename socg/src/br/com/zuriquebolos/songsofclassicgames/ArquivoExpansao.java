package br.com.zuriquebolos.songsofclassicgames;

public class ArquivoExpansao {
	
    public final boolean mIsMain;
    public final int mFileVersion;
    public final long mFileSize;

    public ArquivoExpansao(boolean isMain, int fileVersion, long fileSize) {
        this.mIsMain = isMain;
        this.mFileVersion = fileVersion;
        this.mFileSize = fileSize;
    }
}