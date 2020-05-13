package one.block.androidexampleapp.testImplementation;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContextFreeData implements Serializable {
    @NotNull
    public List<String> rawContextFreeData;

    public ContextFreeData(@NotNull List<String> contextFreeData) {
        this.rawContextFreeData = contextFreeData;
    }

    @NotNull
    public List<String> getContextFreeData() {
        return this.rawContextFreeData;
    }

    @NotNull
    public List<String> getHexContextFreeData() {
        List<String> hexedContextFreeData = new ArrayList<String>();

        for(String cfd : rawContextFreeData) {
            hexedContextFreeData.add(Hex.toHexString(cfd.getBytes()));
        }

        return hexedContextFreeData;
    }

    public String getPackedContextFreeData() {
        if (this.rawContextFreeData.size() == 0) {
            return "";
        }
        List<String> hexContextFreeData = this.getHexContextFreeData();
        String packedContextFreeData = this.getHexPrefix(hexContextFreeData.size());

        for(int i = 0; i < hexContextFreeData.size(); i++) {
            String hexData = hexContextFreeData.get(i);
            packedContextFreeData += this.getHexPrefix(hexData.length() / 2) + hexData;
        }

        return "0180017465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374746573747465737474657374";
        //return packedContextFreeData;
        //return "019d035468697320697320736f6d65206c6f6e6720636f6e746578742066726565206461746120696e7075742e2049742063616e2068617665207768617465766572206461746120796f752077616e7420696e2069742e2049742077696c6c20626520636f70696564206d756c7469706c652074696d657320746f20696e637265617365206c656e6774682e205468697320697320736f6d65206c6f6e6720636f6e746578742066726565206461746120696e7075742e2049742063616e2068617665207768617465766572206461746120796f752077616e7420696e2069742e2049742077696c6c20626520636f70696564206d756c7469706c652074696d657320746f20696e637265617365206c656e6774682e205468697320697320736f6d65206c6f6e6720636f6e746578742066726565206461746120696e7075742e2049742063616e2068617665207768617465766572206461746120796f752077616e7420696e2069742e2049742077696c6c20626520636f70696564206d756c7469706c652074696d657320746f20696e637265617365206c656e6774682e";
    }

    // Splits by 128
    private String getHexPrefix(int length) {

        return String.format("%02X", length);
    }
}
