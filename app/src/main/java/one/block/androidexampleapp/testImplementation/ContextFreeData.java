package one.block.androidexampleapp.testImplementation;

import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;

public class ContextFreeData implements Serializable {
    @NotNull
    public List<String> contextFreeData;

    @NotNull
    public byte[] rawBytes;

    public ContextFreeData(@NotNull List<String> contextFreeData) {
        this.setData(contextFreeData);
    }

    @NotNull
    public List<String> getData() {
        return this.contextFreeData;
    }

    @NotNull
    public byte[] getBytes() {
        return this.rawBytes;
    }


    public void setBytes(byte[] bytes) {
        this.rawBytes = bytes;
    }

    public void setData(List<String> contextFreeData) {
        if (contextFreeData.size() == 0) {
            return;
        }
        this.contextFreeData = contextFreeData;

        ByteBuffer buffer = ByteBuffer.allocate(this.getTotalBytes(this.contextFreeData));

        pushPrefix(buffer, this.contextFreeData.size());

        for(String cfd : this.contextFreeData) {
            byte[] cfdBytes = cfd.getBytes();
            pushPrefix(buffer, cfdBytes.length);
            buffer.put(cfdBytes);
        }

        this.setBytes(buffer.array());
    }

    public String getHexed() {
        if (this.contextFreeData.size() == 0) {
            return "";
        }

        return Hex.toHexString(this.getBytes());
    }

    public String getPacked() {
        if (this.contextFreeData.size() == 0) {
            return "";
        }

        return Hex.toHexString(Sha256Hash.hash(this.getBytes()));
    }

    private void pushPrefix(ByteBuffer buffer, int length) {
        while(true) {
            if (length >>> 7 == 0) {
                buffer.put((byte)length);
                break;
            } else {
                buffer.put((byte)(0x80 | (length & 0x7f)));
                length = length >>> 7;
            }
        }
    }

    private Integer getTotalBytes(List<String> contextFreeData) {
        int bytes =  this.getByteSizePrefix(contextFreeData.size());
        for(String cfd : contextFreeData) {
            byte[] cfdBytes = cfd.getBytes();
            bytes += this.getByteSizePrefix(cfdBytes.length) + cfdBytes.length;
        }
        return bytes;
    }

    private Integer getByteSizePrefix(int length) {
        int size = 0;
        while(true) {
            if (length >>> 7 == 0) {
                size++;
                break;
            } else {
                size++;
                length = length >>> 7;
            }
        }

        return size;
    }
}
