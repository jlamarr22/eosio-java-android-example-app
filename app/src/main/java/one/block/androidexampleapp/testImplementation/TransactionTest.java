package one.block.androidexampleapp.testImplementation;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import one.block.eosiojava.models.rpcProvider.Action;
import one.block.eosiojava.models.rpcProvider.Transaction;
import one.block.eosiojava.models.rpcProvider.response.GetBlockResponse;
import one.block.eosiojava.models.rpcProvider.response.GetInfoResponse;

import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;

/**
 * The Transaction class which has data of actions for each transaction. It holds the serialized
 * action data that will be pushed to the blockchain.
 */
public class TransactionTest extends Transaction {
    @SerializedName("context_free_data")
    @NotNull
    public List<String> contextFreeData;

    public List<String> originalContextFreeData;

    /**
     * Instantiates a new Transaction.
     *
     * @param expiration the expiration
     * @param refBlockNum the ref block num
     * @param refBlockPrefix the ref block prefix
     * @param maxNetUsageWords the max net usage words
     * @param maxCpuUsageMs the max cpu usage ms
     * @param delaySec the delay sec
     * @param contextFreeActions the context free actions
     * @param actions the actions
     * @param transactionExtensions the transaction extensions
     * @param contextFreeData the context free data
     */
    public TransactionTest(@NotNull String expiration, @NotNull BigInteger refBlockNum,
                       @NotNull BigInteger refBlockPrefix,
                       @NotNull BigInteger maxNetUsageWords,
                       @NotNull BigInteger maxCpuUsageMs, @NotNull BigInteger delaySec,
                       @NotNull List<Action> contextFreeActions,
                       @NotNull List<Action> actions, @NotNull List<String> transactionExtensions,
                       @NotNull List<String> contextFreeData) {
        super(expiration, refBlockNum, refBlockPrefix, maxNetUsageWords, maxCpuUsageMs, delaySec, contextFreeActions, actions, transactionExtensions);
        this.setContextFreeData(contextFreeData);
    }

    /**
     * Instantiates a new Transaction.
     *
     * @param expiration the expiration
     * @param refBlockNum the ref block num
     * @param refBlockPrefix the ref block prefix
     * @param maxNetUsageWords the max net usage words
     * @param maxCpuUsageMs the max cpu usage ms
     * @param delaySec the delay sec
     * @param contextFreeActions the context free actions
     * @param actions the actions
     * @param transactionExtensions the transaction extensions
     */
    public TransactionTest(@NotNull String expiration, @NotNull BigInteger refBlockNum,
                       @NotNull BigInteger refBlockPrefix,
                       @NotNull BigInteger maxNetUsageWords,
                       @NotNull BigInteger maxCpuUsageMs, @NotNull BigInteger delaySec,
                       @NotNull List<Action> contextFreeActions,
                       @NotNull List<Action> actions, @NotNull List<String> transactionExtensions) {
        this(expiration, refBlockNum, refBlockPrefix, maxNetUsageWords, maxCpuUsageMs, delaySec,
                contextFreeActions, actions, transactionExtensions, new ArrayList<String>());
    }

    @NotNull
    public List<String> getContextFreeData() { return contextFreeData; }

    public void setContextFreeData(@NotNull List<String> contextFreeData) {
        List<String> serializedContextFreeData = new ArrayList<String>();

        for(String cfd : contextFreeData) {
            serializedContextFreeData.add(Hex.toHexString(cfd.getBytes()));
        }

        this.contextFreeData = serializedContextFreeData;
        this.originalContextFreeData = contextFreeData;
    }

    // This will need to be updated to be dynamic
    public String getPackedContextFreeData() {
        String packedContextFreeData = String.format("%02X", this.contextFreeData.size());

        for(int i = 0; i < this.contextFreeData.size(); i++) {
            String cfd = this.contextFreeData.get(i);
            packedContextFreeData += String.format("%02X", cfd.length() / 2) + cfd;
        }

        //return "01207b226368616c6c656e676572223a202231222c2022686f7374223a202232227d";
        return packedContextFreeData;
    }

    public String getHexContextFreeData() {
        byte[] bytes = new byte[this.getTotalBytes()];
        bytes[0] = Byte.parseByte(String.valueOf(this.contextFreeData.size()));
        int index = 1;
        for(String cfd : this.originalContextFreeData) {
            byte[] cfdBytes = cfd.getBytes();
            bytes[index] = Byte.parseByte(String.valueOf(cfdBytes.length));
            index++;
            for (int i = 0; i < cfdBytes.length; i++) {
                bytes[index] = cfdBytes[i];
                index++;
            }
        }

        return Hex.toHexString(Sha256Hash.hash(bytes));
    }

    private Integer getTotalBytes() {
        int bytes = 1;
        for(String cfd : this.originalContextFreeData) {
            bytes += 1 + cfd.getBytes().length;
        }
        return bytes;
    }
}

