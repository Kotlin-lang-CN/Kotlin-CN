package tech.kotlin.common.encode;

public interface BytesEncoder {

    byte[] decode(byte[] data, int offset, int len);

    byte[] encode(byte[] data, int offset, int len);

}