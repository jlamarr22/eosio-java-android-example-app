package one.block.androidexampleapp;

import android.os.AsyncTask;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;

import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;

import one.block.androidexampleapp.ErrorUtils;
import one.block.androidexampleapp.testImplementation.EosioJavaRpcProviderImplTest;
import one.block.androidexampleapp.testImplementation.SoftKeySignatureProviderImplTest;
import one.block.androidexampleapp.testImplementation.TransactionProcessorTest;
import one.block.androidexampleapp.testImplementation.TransactionSessionTest;
import one.block.eosiojava.error.EosioError;
import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojava.error.session.TransactionPrepareError;
import one.block.eosiojava.error.session.TransactionSignAndBroadCastError;
import one.block.eosiojava.implementations.ABIProviderImpl;
import one.block.eosiojava.interfaces.IABIProvider;
import one.block.eosiojava.interfaces.IRPCProvider;
import one.block.eosiojava.interfaces.ISerializationProvider;
import one.block.eosiojava.interfaces.ISignatureProvider;
import one.block.eosiojava.models.rpcProvider.Action;
import one.block.eosiojava.models.rpcProvider.Authorization;
import one.block.eosiojava.models.rpcProvider.Transaction;
import one.block.eosiojava.models.rpcProvider.response.PushTransactionResponse;
import one.block.eosiojava.models.rpcProvider.response.RPCResponseError;
import one.block.eosiojava.session.TransactionProcessor;
import one.block.eosiojava.session.TransactionSession;
import one.block.eosiojavaabieosserializationprovider.AbiEosSerializationProviderImpl;
import one.block.eosiojavarpcprovider.error.EosioJavaRpcProviderInitializerError;
import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl;
import one.block.eosiosoftkeysignatureprovider.SoftKeySignatureProviderImpl;
import one.block.eosiosoftkeysignatureprovider.error.ImportKeyError;

/**
 * This class is an example about the most basic/easy way to use eosio-java to send a transaction.
 * <p>
 * Basic steps:
 * <p>
 *     - Create serialization provider as an instant of {@link AbiEosSerializationProviderImpl} from [eosiojavaandroidabieosserializationprovider] library
 *     <p>
 *     - Create RPC provider as an instant of {@link EosioJavaRpcProviderImpl} with an input string point to a node backend.
 *     <p>
 *     - Create ABI provider as an instant of {@link ABIProviderImpl} with instants of Rpc provider and serialization provider.
 *     <p>
 *     - Create Signature provider as an instant of {@link SoftKeySignatureProviderImpl} which is not recommended for production because of its simple key management.
 *     <p>
 *         - Import an EOS private key which associate with sender's account which will be used to sign the transaction.
 * <p>
 *     - Create an instant of {@link TransactionSession} which is used for spawning/factory {@link TransactionProcessor}
 * <p>
 *     - Create an instant of {@link TransactionProcessor} from the instant of {@link TransactionSession} above by calling {@link TransactionSession#getTransactionProcessor()} or {@link TransactionSession#getTransactionProcessor(Transaction)} if desire to use a preset {@link Transaction} object.
 * <p>
 *     - Call {@link TransactionProcessor#prepare(List)} with a list of Actions which is desired to be sent to backend. The method will serialize the list of action to list of hex and keep them inside
 * the list of {@link Transaction#getActions()}. The transaction now is ready to be signed and broadcast.
 * <p>
 *     - Call {@link TransactionProcessor#signAndBroadcast()} to sign the transaction inside {@link TransactionProcessor} and broadcast it to backend.
 */
public class TransactionTask extends AsyncTask<String, String, Void> {

    /**
     * Whether the network logs will be enabled for RPC provider
     */
    private static final boolean ENABLE_NETWORK_LOG = true;

    private TransactionTaskCallback callback;

    public TransactionTask(@NonNull TransactionTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (values.length == 1) {
            String message = values[0];
            this.callback.update(message);
        } else if (values.length == 2) {
            boolean isSuccess = Boolean.parseBoolean(values[0]);
            String message = values[1];
            this.callback.finish(isSuccess, message);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        String nodeUrl = params[0];
        String fromAccount = params[1];
        String toAccount = params[2];
        String privateKey = params[3];
        String amount = params[4];
        String memo = params[5];
        String exampleAccount = "example";
        String account = "tictactoe";
        String propertyName = "aproperty";
        String challenger = "opponent";
        String host = "host";

        this.publishProgress("Transferring " + amount + " to " + toAccount);

        // Creating serialization provider
        ISerializationProvider serializationProvider;
        try {
            serializationProvider = new AbiEosSerializationProviderImpl();
        } catch (SerializationProviderError serializationProviderError) {
            serializationProviderError.printStackTrace();
            return null;
        }

        // Creating RPC Provider
        EosioJavaRpcProviderImplTest rpcProvider;
        try {
            rpcProvider = new EosioJavaRpcProviderImplTest(nodeUrl, ENABLE_NETWORK_LOG);
        } catch (EosioJavaRpcProviderInitializerError eosioJavaRpcProviderInitializerError) {
            eosioJavaRpcProviderInitializerError.printStackTrace();
            this.publishProgress(Boolean.toString(false), eosioJavaRpcProviderInitializerError.getMessage());
            return null;
        }

        // Creating ABI provider
        IABIProvider abiProvider = new ABIProviderImpl(rpcProvider, serializationProvider);

        // Creating Signature provider
        SoftKeySignatureProviderImplTest signatureProvider = new SoftKeySignatureProviderImplTest();

        try {
            ((SoftKeySignatureProviderImplTest) signatureProvider).importKey(privateKey);
        } catch (ImportKeyError importKeyError) {
            importKeyError.printStackTrace();
            this.publishProgress(Boolean.toString(false), importKeyError.getMessage());
            return null;
        }

        // Creating TransactionProcess
        TransactionSessionTest session = new TransactionSessionTest(serializationProvider, rpcProvider, abiProvider, signatureProvider);
        TransactionProcessorTest processor = session.getTransactionProcessor();

        // Apply transaction data to Action's data
        String jsonData = "{\n" +
                "\"challenger\": \"" + challenger + "\",\n" +
                "\"host\": \"" + host + "\",\n" +
                "\"by\": \"" + host + "\"\n" +
                "}";

        this.publishProgress("jsonData: " + jsonData);

//        String contextFreeData = "{\n" +
//                "\"challenger\": \"" + challenger + "\",\n" +
//                "\"host\": \"" + host + "\"\n" +
//                "}";

        String contextFreeData = "test";
        String contextFreeData2 = "{\"challenger\": \"test\", \"host\": \"test2\"}";

        ArrayList<String> cfd = new ArrayList<String>();
        cfd.add(contextFreeData);
        cfd.add(contextFreeData2);

        // Creating action with action's data, eosio.token contract and transfer action.
        Action action = new Action(account, "contextfree", Collections.singletonList(new Authorization(account, "active")), jsonData);
        try {
            this.publishProgress("Preparing Transaction...");
            processor.prepare(Collections.singletonList(action), new ArrayList<Action>(), cfd);

            // Sign and broadcast the transaction.
            this.publishProgress("Signing and Broadcasting Transaction...");
            PushTransactionResponse response = processor.signAndBroadcast();

            this.publishProgress(Boolean.toString(true), "Finished!  Your transaction id is:  " + response.getTransactionId());
        } catch (TransactionPrepareError transactionPrepareError) {
            // Happens if preparing transaction unsuccessful
            transactionPrepareError.printStackTrace();
            this.publishProgress(Boolean.toString(false), transactionPrepareError.getLocalizedMessage());
        } catch (TransactionSignAndBroadCastError transactionSignAndBroadCastError) {
            // Happens if Sign transaction or broadcast transaction unsuccessful.
            transactionSignAndBroadCastError.printStackTrace();

            // try to get backend error if the error come from backend
            RPCResponseError rpcResponseError = ErrorUtils.getBackendError((EosioError) transactionSignAndBroadCastError);
            if (rpcResponseError != null) {
                String backendErrorMessage = ErrorUtils.getBackendErrorMessageFromResponse(rpcResponseError);
                this.publishProgress(Boolean.toString(false), backendErrorMessage);
                return null;
            }

            this.publishProgress(Boolean.toString(false), transactionSignAndBroadCastError.getMessage());
        }

        return null;
    }

    public interface TransactionTaskCallback {
        void update(String updateContent);

        void finish(boolean success, String updateContent);
    }
}
