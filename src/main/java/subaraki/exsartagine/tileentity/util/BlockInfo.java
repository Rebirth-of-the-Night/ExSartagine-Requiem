package subaraki.exsartagine.tileentity.util;

public class BlockInfo {

    public final boolean hot;
    public final boolean legs;

    public static final BlockInfo INVALID = new BlockInfo(false,false);

    public BlockInfo(boolean hot, boolean legs) {
        this.hot = hot;
        this.legs = legs;
    }
}
