package tech.kotlin.utils.encode;

public class RC4Encoder implements BytesEncoder {

    private final RC4 encoder;
    private final RC4 decoder;

    public RC4Encoder(byte[] key) {
        this.encoder = new RC4(key);
        this.decoder = new RC4(key);
    }

    @Override
    public byte[] decode(byte[] data, int offset, int len) {
        return decoder.flip(data, offset, len);
    }

    @Override
    public byte[] encode(byte[] data, int offset, int len) {
        return encoder.flip(data, offset, len);
    }

    class RC4 {

        private final byte[] S = new byte[256];
        private final byte[] T = new byte[256];
        private final int keyLength;
        int i = 0, j = 0, k = 0, t = 0;
        byte tmp;

        RC4(final byte[] key) {
            if (key.length < 1 || key.length > 256) {
                throw new IllegalArgumentException("key must be between 1 and 256 bytes");
            } else {
                keyLength = key.length;
                for (int i = 0; i < 256; i++) {
                    S[i] = (byte) i;
                    T[i] = key[i % keyLength];
                }
                int j = 0;
                byte tmp;
                for (int i = 0; i < 256; i++) {
                    j = (j + S[i] + T[i]) & 0xFF;
                    tmp = S[j];
                    S[j] = S[i];
                    S[i] = tmp;
                }
            }
        }

        byte[] flip(final byte[] plaintext, int offset, int len) {
            final byte[] cipher = new byte[len];
            for (int counter = 0; counter < len; counter++) {
                i = (i + 1) & 0xFF;
                j = (j + S[i]) & 0xFF;
                tmp = S[j];
                S[j] = S[i];
                S[i] = tmp;
                t = (S[i] + S[j]) & 0xFF;
                k = S[t];
                cipher[counter] = (byte) (plaintext[counter + offset] ^ k);
            }
            return cipher;
        }
    }
}