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
import org.jetbrains.annotations.NotNull;

/**
 * The Transaction class which has data of actions for each transaction. It holds the serialized
 * action data that will be pushed to the blockchain.
 */
public class TransactionTest extends Transaction {
    @SerializedName("context_free_data")
    @NotNull
    public List<String> contextFreeData;

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
        //this.contextFreeData = new String[] { "41424344", "4142434445" };
        //this.contextFreeData = "020441424344054142434445";
        this.contextFreeData = contextFreeData;
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
        this.contextFreeData = contextFreeData;
    }
}

