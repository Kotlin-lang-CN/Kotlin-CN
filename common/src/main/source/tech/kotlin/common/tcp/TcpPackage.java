package tech.kotlin.common.tcp;

import java.util.Locale;

public class TcpPackage {

    public final int length;
    public final int type;
    public final byte[] data;

    public TcpPackage(int type, byte[] data) {
        this.length = data.length;
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format(Locale.CHINA, "len=%d;type=%d", length, type);
    }
}