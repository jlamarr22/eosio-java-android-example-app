package one.block.androidexampleapp.testImplementation;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ContextFreeData implements Serializable {
    @NotNull
    public List<String> rawContextFreeData;

    @NotNull
    public byte[] rawBytes;

    public ContextFreeData(@NotNull List<String> contextFreeData) {
        this.rawContextFreeData = contextFreeData;
    }

    @NotNull
    public List<String> getContextFreeData() {
        return this.rawContextFreeData;
    }

    @NotNull
    public byte[] getBytes() {
        return this.rawBytes;
    }


    public void setBytes(byte[] bytes) {
        this.rawBytes = bytes;
    }

//    @NotNull
//    public List<String> getHexContextFreeData() {
//        List<String> hexedContextFreeData = new ArrayList<String>();
//
//        for(String cfd : rawContextFreeData) {
//            hexedContextFreeData.add(Hex.toHexString(cfd.getBytes()));
//        }
//
//        return hexedContextFreeData;
//    }

    public String getPackedContextFreeData() {
        if (this.rawContextFreeData.size() == 0) {
            return "";
        }

        return Hex.toHexString(this.rawBytes);
    }

    // Splits by 128
    private String getHexPrefix(int length) {

        return String.format("%02X", length);
    }

    public String getHexContextFreeData() {
        if (this.rawContextFreeData.size() == 0) {
            return "";
        }

        ByteBuffer buffer = ByteBuffer.allocate(this.getTotalBytes(this.rawContextFreeData));

        pushPrefix(buffer, this.rawContextFreeData.size());

        for(String cfd : this.rawContextFreeData) {
            byte[] cfdBytes = cfd.getBytes();
            pushPrefix(buffer, cfdBytes.length);
            buffer.put(cfdBytes);
        }

        this.setBytes(buffer.array());

        return Hex.toHexString(Sha256Hash.hash(buffer.array()));
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
