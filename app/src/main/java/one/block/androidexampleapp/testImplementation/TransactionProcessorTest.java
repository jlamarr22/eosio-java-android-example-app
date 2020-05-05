package one.block.androidexampleapp.testImplementation;

import com.google.common.base.Strings;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import one.block.eosiojava.error.ErrorConstants;
import one.block.eosiojava.error.abiProvider.GetAbiError;
import one.block.eosiojava.error.rpcProvider.GetBlockRpcError;
import one.block.eosiojava.error.rpcProvider.GetInfoRpcError;
import one.block.eosiojava.error.rpcProvider.GetRequiredKeysRpcError;
import one.block.eosiojava.error.rpcProvider.PushTransactionRpcError;
import one.block.eosiojava.error.serializationProvider.DeserializeTransactionError;
import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojava.error.serializationProvider.SerializeError;
import one.block.eosiojava.error.serializationProvider.SerializeTransactionError;
import one.block.eosiojava.error.session.TransactionBroadCastEmptySignatureError;
import one.block.eosiojava.error.session.TransactionBroadCastError;
import one.block.eosiojava.error.session.TransactionCreateSignatureRequestAbiError;
import one.block.eosiojava.error.session.TransactionCreateSignatureRequestEmptyAvailableKeyError;
import one.block.eosiojava.error.session.TransactionCreateSignatureRequestError;
import one.block.eosiojava.error.session.TransactionCreateSignatureRequestKeyError;
import one.block.eosiojava.error.session.TransactionCreateSignatureRequestRequiredKeysEmptyError;
import one.block.eosiojava.error.session.TransactionCreateSignatureRequestRpcError;
import one.block.eosiojava.error.session.TransactionCreateSignatureRequestSerializationError;
import one.block.eosiojava.error.session.TransactionGetSignatureDeserializationError;
import one.block.eosiojava.error.session.TransactionGetSignatureError;
import one.block.eosiojava.error.session.TransactionGetSignatureNotAllowModifyTransactionError;
import one.block.eosiojava.error.session.TransactionGetSignatureSigningError;
import one.block.eosiojava.error.session.TransactionPrepareError;
import one.block.eosiojava.error.session.TransactionPrepareInputError;
import one.block.eosiojava.error.session.TransactionPrepareRpcError;
import one.block.eosiojava.error.session.TransactionProcessorConstructorInputError;
import one.block.eosiojava.error.session.TransactionPushTransactionError;
import one.block.eosiojava.error.session.TransactionSerializeError;
import one.block.eosiojava.error.session.TransactionSignAndBroadCastError;
import one.block.eosiojava.error.session.TransactionSignError;
import one.block.eosiojava.error.signatureProvider.GetAvailableKeysError;
import one.block.eosiojava.error.signatureProvider.SignatureProviderError;
import one.block.eosiojava.interfaces.IABIProvider;
import one.block.eosiojava.interfaces.IRPCProvider;
import one.block.eosiojava.interfaces.ISerializationProvider;
import one.block.eosiojava.interfaces.ISignatureProvider;
import one.block.eosiojava.models.AbiEosSerializationObject;
import one.block.eosiojava.models.EOSIOName;
import one.block.eosiojava.models.rpcProvider.Action;
import one.block.eosiojava.models.rpcProvider.Transaction;
import one.block.eosiojava.models.rpcProvider.TransactionConfig;
import one.block.eosiojava.models.rpcProvider.request.GetBlockRequest;
import one.block.eosiojava.models.rpcProvider.request.GetRequiredKeysRequest;
import one.block.eosiojava.models.rpcProvider.request.PushTransactionRequest;
import one.block.eosiojava.models.rpcProvider.response.GetBlockResponse;
import one.block.eosiojava.models.rpcProvider.response.GetInfoResponse;
import one.block.eosiojava.models.rpcProvider.response.GetRequiredKeysResponse;
import one.block.eosiojava.models.rpcProvider.response.PushTransactionResponse;
import one.block.eosiojava.models.signatureProvider.EosioTransactionSignatureRequest;
import one.block.eosiojava.models.signatureProvider.EosioTransactionSignatureResponse;
import one.block.eosiojava.session.TransactionProcessor;
import one.block.eosiojava.utilities.DateFormatter;
import one.block.eosiojava.utilities.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper class for an EOS Transaction.  This class allows the developer to use the Transaction
 * object in several ways.
 * <p>
 * The TransactionProcessor allows the developer to:
 * <p>
 * - Get input {@link one.block.eosiojava.models.rpcProvider.Action}
 * <p>
 * - Create transaction
 * <p>
 * - Serialize Transaction
 * <p>
 * - Sign Transaction
 * <p>
 * - Broadcast Transaction
 */
public class TransactionProcessorTest {

    /**
     * Reference of Serialization Provider from TransactionSession
     */
    @NotNull
    private ISerializationProvider serializationProvider;

    /**
     * Reference of RPC Provider from TransactionSession
     */
    @NotNull
    private IRPCProvider rpcProvider;

    /**
     * Reference of ABI Provider from TransactionSession
     */
    @NotNull
    private IABIProvider abiProvider;

    /**
     * Reference of Signature Provider from TransactionSession
     */
    @NotNull
    private ISignatureProvider signatureProvider;

    /**
     * Transaction instance that holds all data relating to an EOS Transaction.
     * <p>
     * This object holds the non-serialized version of the transaction.  However, the serialized
     * version can be extracted by calling the serialize() {@link TransactionProcessorTest#serialize()}
     * method.
     */
    @Nullable
    private TransactionTest transaction;

    /**
     * This is an original instance of the transaction that is populated once the signature
     * provider returns the signed transaction.
     *
     * Check getSignature() flow in "complete workflow" doc for more detail
     */
    @Nullable
    private TransactionTest originalTransaction;

    /**
     * List of signatures used to sign the transaction.  This is populated after the transaction
     * has been signed and the signatures have been return by the signature provider.
     * <p>
     * Check getSignature() flow in "Complete Workflow" document for more detail.
     */
    @NotNull
    private List<String> signatures = new ArrayList<>();

    /**
     * The serialized version of the Transaction that is used to determine whether or not the
     * signature provider modified the transaction.
     * <p>
     *     If the transaction is updated, this object needs to be cleared or updated.
     * <p>
     *     Check getSignature() flow in "Complete Workflow" document for more detail
     * about the value assigned and usages.
     */
    @Nullable
    private String serializedTransaction;

    /**
     * List of available keys that may be provided by SignatureProvider.
     * <p>
     * If this list is already set, the TransactionProcessor won't ask for available keys from
     * the signature provider and use this list.
     * <p> Check createSignatureRequest() flow in "Complete Workflow" doc for more
     * detail.
     */
    @Nullable
    private List<String> availableKeys;

    /**
     * List of required keys to sign the transaction.  See
     * {@link IRPCProvider#getRequiredKeys(GetRequiredKeysRequest)}
     * <p>
     * If this list is set, TransactionProcessor will use this list instead of a making an RPC
     * call to get the required keys.
     * <p>
     * Check the createSignatureRequest() flow in "Complete Workflow" doc for more detail.
     */
    @Nullable
    private List<String> requiredKeys;

    /**
     * Configuration for transaction which offers ability to set:
     * <p>
     * - The expiration period for the transaction in seconds
     * <p>
     * - How many blocks behind
     */
    @NotNull
    private TransactionConfig transactionConfig = new TransactionConfig();

    /**
     * Chain id of target blockchain that will be used in createSignatureRequest()
     */
    @Nullable
    private String chainId;

    /**
     * Whether or not to allow the signature provider to modify the transaction.
     * <p>
     * Default is false.
     */
    private boolean isTransactionModificationAllowed;

    /**
     * Constructor with all provider references from
     * @param serializationProvider the serialization provider.
     * @param rpcProvider the rpc provider.
     * @param abiProvider the abi provider.
     * @param signatureProvider the signature provider.
     */
    public TransactionProcessorTest(
            @NotNull ISerializationProvider serializationProvider,
            @NotNull IRPCProvider rpcProvider,
            @NotNull IABIProvider abiProvider,
            @NotNull ISignatureProvider signatureProvider) {
        this.serializationProvider = serializationProvider;
        this.rpcProvider = rpcProvider;
        this.abiProvider = abiProvider;
        this.signatureProvider = signatureProvider;
    }

    /**
     * Constructor with all provider references from
     * Transaction
     * @param serializationProvider the serialization provider.
     * @param rpcProvider the rpc provider.
     * @param abiProvider the abi provider.
     * @param signatureProvider the signature provider.
     * @param transaction - preset Transaction
     * @throws TransactionProcessorConstructorInputError thrown if the input transaction has an empty action list.
     */
    public TransactionProcessorTest(
            @NotNull ISerializationProvider serializationProvider,
            @NotNull IRPCProvider rpcProvider,
            @NotNull IABIProvider abiProvider,
            @NotNull ISignatureProvider signatureProvider,
            @NotNull TransactionTest transaction) throws TransactionProcessorConstructorInputError {
        this(serializationProvider, rpcProvider, abiProvider, signatureProvider);
        this.transaction = transaction;
        if (this.transaction.getActions().isEmpty()) {
            throw new TransactionProcessorConstructorInputError(
                    ErrorConstants.TRANSACTION_PROCESSOR_ACTIONS_EMPTY_ERROR_MSG);
        }
    }

    //region public methods

    public void prepare(@NotNull List<Action> actions, @NotNull List<Action> contextFreeActions) throws TransactionPrepareError {
        prepare(actions, contextFreeActions, "");
    }

    /**
     * Prepare action's data from input and create new instance of Transaction if it is not set.
     * <p>
     * Check prepare() flow in "Complete Workflow" doc for more detail
     *
     * @param actions - List of actions with data. If the transaction is preset or has a value and it has its own actions, that list will be over-ridden by this input list.
     * @param contextFreeActions - List of context free actions with data.
     *
     * @throws TransactionPrepareError thrown if:
     *          <br>
     *              - chainId from {@link IRPCProvider#getInfo()} is blank
     *          <br>
     *              - chainId returned from the chain does not match with input chainId
     *          <br>
     *              - There is a problem with parsing head block time from {@link GetInfoResponse#getHeadBlockTime()}
     *          <br>
     *          It throws a base error class if:
     *          <br>
     *              {@link TransactionPrepareInputError} thrown if inputs are invalid
     *              <br>
     *              {@link TransactionPrepareRpcError} thrown if any RPC call ({@link IRPCProvider#getInfo()}
     *              and {@link IRPCProvider#getBlock(GetBlockRequest)}) return or throw an error
     */
    public void prepare(@NotNull List<Action> actions, @NotNull List<Action> contextFreeActions, String contextFreeData) throws TransactionPrepareError {
        if (actions.isEmpty()) {
            throw new TransactionPrepareInputError(
                    ErrorConstants.TRANSACTION_PROCESSOR_ACTIONS_EMPTY_ERROR_MSG);
        }

        /*
         Create new instance of Transaction.
         Final value will be assigned to transaction object in class level and override preset
         Transaction if it was set by constructor.  Modifying a new transaction avoids corrupting the
         original if an exception is encountered during the modification process.
        */
        TransactionTest preparingTransaction = new TransactionTest("", BigInteger.ZERO, BigInteger.ZERO,
                BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, contextFreeActions, actions,
                new ArrayList<String>(), contextFreeData);

        // Assigning values for transaction expiration, refBlockNum and refBlockPrefix
        GetInfoResponse getInfoResponse;

        try {
            getInfoResponse = this.rpcProvider.getInfo();
        } catch (GetInfoRpcError getInfoRpcError) {
            throw new TransactionPrepareRpcError(ErrorConstants.TRANSACTION_PROCESSOR_RPC_GET_INFO,
                    getInfoRpcError);
        }

        if (Strings.isNullOrEmpty(this.chainId)) {
            if (Strings.isNullOrEmpty(getInfoResponse.getChainId())) {
                // Throw exception if both provided chain id and RPC chain id are empty
                throw new TransactionPrepareError(
                        ErrorConstants.TRANSACTION_PROCESSOR_PREPARE_CHAINID_RPC_EMPTY);
            }

            // Assign value to chain id only if chain id is not provided and RPC's chain id is valid
            this.chainId = getInfoResponse.getChainId();
        } else if (!Strings.isNullOrEmpty(getInfoResponse.getChainId()) && !getInfoResponse
                .getChainId().equals(chainId)) {
            // Throw error if both are not empty but one does not match with another
            throw new TransactionPrepareError(
                    String.format(ErrorConstants.TRANSACTION_PROCESSOR_PREPARE_CHAINID_NOT_MATCH,
                            this.chainId,
                            getInfoResponse.getChainId()));
        }

        if (preparingTransaction.getExpiration().isEmpty()) {
            String strHeadBlockTime = getInfoResponse.getHeadBlockTime();

            long headBlockTime;

            try {
                headBlockTime = DateFormatter.convertBackendTimeToMilli(strHeadBlockTime);
            } catch (ParseException e) {
                throw new TransactionPrepareError(
                        ErrorConstants.TRANSACTION_PROCESSOR_HEAD_BLOCK_TIME_PARSE_ERROR, e);
            }

            int expiresSeconds = this.transactionConfig.getExpiresSeconds();

            long expirationTimeInMilliseconds = headBlockTime + expiresSeconds * 1000;
            preparingTransaction.setExpiration(DateFormatter
                    .convertMilliSecondToBackendTimeString(expirationTimeInMilliseconds));
        }

        // Assigning value to refBlockNum and refBlockPrefix

        BigInteger headBlockNum;

        int blockBehindConfig = this.transactionConfig.getBlocksBehind();

        if (getInfoResponse.getHeadBlockNum().compareTo(BigInteger.valueOf(blockBehindConfig))
                > 0) {
            headBlockNum = getInfoResponse.getHeadBlockNum()
                    .subtract(BigInteger.valueOf(blockBehindConfig));
        } else {
            headBlockNum = BigInteger.valueOf(blockBehindConfig);
        }

        GetBlockResponse getBlockResponse;
        try {
            getBlockResponse = this.rpcProvider
                    .getBlock(new GetBlockRequest(headBlockNum.toString()));
        } catch (GetBlockRpcError getBlockRpcError) {
            throw new TransactionPrepareRpcError(
                    ErrorConstants.TRANSACTION_PROCESSOR_PREPARE_RPC_GET_BLOCK, getBlockRpcError);
        }

        // Restrict the refBlockNum to 32 bit unsigned value
        BigInteger refBlockNum = getBlockResponse.getBlockNum().and(BigInteger.valueOf(0xffff));
        BigInteger refBlockPrefix = getBlockResponse.getRefBlockPrefix();

        preparingTransaction.setRefBlockNum(refBlockNum);
        preparingTransaction.setRefBlockPrefix(refBlockPrefix);

        this.finishPreparing(preparingTransaction);
    }

    /**
     * Prepare action's data from input and create new instance of Transaction if it is not set.
     * <p>
     *     Use this method if you don't want to provide context free actions.
     * <p>
     * Check prepare() flow in "Complete Workflow" doc for more detail.
     *
     * @param actions - List of actions with data. If the transaction is preset or has a value and it
     * has its own actions, that list will be over-ridden by this input list.
     * @throws TransactionPrepareError thrown if:
     *          <br>
     *              - chainId from {@link IRPCProvider#getInfo()} is blank
     *          <br>
     *              - chainId returned from the chain does not match with input chainId
     *          <br>
     *              - There is a problem with parsing head block time from {@link GetInfoResponse#getHeadBlockTime()}
     *          <br>
     *          It throws a base error class if:
     *          <br>
     *              {@link TransactionPrepareInputError} thrown if input is invalid
     *              <br>
     *              {@link TransactionPrepareRpcError} thrown if any RPC call ({@link IRPCProvider#getInfo()}
     *              and {@link IRPCProvider#getBlock(GetBlockRequest)}) return or throw an error
     */
    public void prepare(@NotNull List<Action> actions) throws TransactionPrepareError {
        this.prepare(actions, new ArrayList<Action>());
    }

    /**
     * Sign the transaction by passing {@link EosioTransactionSignatureRequest} to signature
     * provider
     * <p>
     * Check sign() flow in "Complete Workflow" document for more detail
     *
     * @return success or not
     * @throws TransactionSignError thrown if there are any exceptions during the following:
     *      <br>
     *          - Creating signature. Cause: {@link TransactionCreateSignatureRequestError}
     *      <br>
     *          - Signing. Cause: {@link TransactionGetSignatureError} or {@link SignatureProviderError}
     */
    public boolean sign() throws TransactionSignError {
        EosioTransactionSignatureRequest eosioTransactionSignatureRequest;
        try {
            eosioTransactionSignatureRequest = this.createSignatureRequest();
        } catch (TransactionCreateSignatureRequestError transactionCreateSignatureRequestError) {
            throw new TransactionSignError(
                    ErrorConstants.TRANSACTION_PROCESSOR_SIGN_CREATE_SIGN_REQUEST_ERROR,
                    transactionCreateSignatureRequestError);
        }

        EosioTransactionSignatureResponse eosioTransactionSignatureResponse;
        try {
            eosioTransactionSignatureResponse = this.getSignature(eosioTransactionSignatureRequest);
            if (eosioTransactionSignatureResponse.getError() != null) {
                throw eosioTransactionSignatureResponse.getError();
            }
        } catch (TransactionGetSignatureError transactionGetSignatureError) {
            throw new TransactionSignError(transactionGetSignatureError);
        } catch (@Nullable SignatureProviderError signatureProviderError) {
            throw new TransactionSignError(
                    ErrorConstants.TRANSACTION_PROCESSOR_SIGN_SIGNATURE_RESPONSE_ERROR,
                    signatureProviderError);
        }

        return true;
    }

    /**
     * Broadcast transaction to blockchain. <p> Check broadcast() flow in "Complete Workflow"
     * document for more detail.
     *
     * @return broadcast result from chain
     * @throws TransactionBroadCastError thrown under the following conditions:
     *      <br>
     *          - The transaction has not been prepared or serialized yet.
     *      <br>
     *          - The transaction has not been signed yet (no signature).
     *      <br>
     *          - An error has been returned from the blockchain. Cause: {@link TransactionPushTransactionError}
     */
    @NotNull
    public PushTransactionResponse broadcast() throws TransactionBroadCastError {
        if (this.serializedTransaction == null || this.serializedTransaction.isEmpty()) {
            throw new TransactionBroadCastError(
                    ErrorConstants.TRANSACTION_PROCESSOR_BROADCAST_SERIALIZED_TRANSACTION_EMPTY);
        }

        if (this.signatures.isEmpty()) {
            throw new TransactionBroadCastEmptySignatureError(
                    ErrorConstants.TRANSACTION_PROCESSOR_BROADCAST_SIGN_EMPTY);
        }

        PushTransactionRequest pushTransactionRequest = new PushTransactionRequest(this.signatures,
                0, this.transaction.getContextFreeData(), this.serializedTransaction);
        try {
            return this.pushTransaction(pushTransactionRequest);
        } catch (TransactionPushTransactionError transactionPushTransactionError) {
            throw new TransactionBroadCastError(
                    ErrorConstants.TRANSACTION_PROCESSOR_BROADCAST_TRANS_ERROR,
                    transactionPushTransactionError);
        }
    }

    /**
     * Sign and broadcast the transaction and signature/s to chain
     *
     * @return PushTransactionResponse from blockchain.
     * @throws TransactionSignAndBroadCastError thrown under the following conditions:
     *      <br>
     *          - Exception while creating signature. Cause: {@link TransactionCreateSignatureRequestError}
     *      <br>
     *          - Exception while signing. Cause: {@link TransactionGetSignatureError} or {@link SignatureProviderError}
     *      <br>
     *          - The transaction has not been prepared or serialized yet.
     *      <br>
     *          - The transaction has not been signed yet (no signature).
     *      <br>
     *          - An error has been returned from the blockchain. Cause: {@link TransactionPushTransactionError}
     */
    @NotNull
    public PushTransactionResponse signAndBroadcast() throws TransactionSignAndBroadCastError {
        EosioTransactionSignatureRequest eosioTransactionSignatureRequest;
        try {
            eosioTransactionSignatureRequest = this.createSignatureRequest();
        } catch (TransactionCreateSignatureRequestError transactionCreateSignatureRequestError) {
            throw new TransactionSignAndBroadCastError(transactionCreateSignatureRequestError);
        }

        try {
            this.getSignature(eosioTransactionSignatureRequest);
        } catch (TransactionGetSignatureError transactionGetSignatureError) {
            throw new TransactionSignAndBroadCastError(transactionGetSignatureError);
        }

        if (this.serializedTransaction == null || this.serializedTransaction.isEmpty()) {
            throw new TransactionSignAndBroadCastError(
                    ErrorConstants.TRANSACTION_PROCESSOR_SIGN_BROADCAST_SERIALIZED_TRANSACTION_EMPTY);
        }

        if (this.signatures.isEmpty()) {
            throw new TransactionSignAndBroadCastError(
                    ErrorConstants.TRANSACTION_PROCESSOR_SIGN_BROADCAST_SIGN_EMPTY);
        }

        // Signatures and serializedTransaction are assigned and finalized in getSignature() method
        PushTransactionRequest pushTransactionRequest = new PushTransactionRequest(this.signatures,
                0, this.transaction.getContextFreeData(), this.serializedTransaction);
        try {
            return this.pushTransaction(pushTransactionRequest);
        } catch (TransactionPushTransactionError transactionPushTransactionError) {
            throw new TransactionSignAndBroadCastError(transactionPushTransactionError);
        }
    }

    /**
     * Return transaction in JSON string format.
     *
     * @return JSON String of transaction
     */
    @Nullable
    public String toJSON() {
        return Utils.getGson(DateFormatter.BACKEND_DATE_PATTERN).toJson(this.transaction);
    }

    /**
     * Getting serialized version of Transaction <p> Check serialize() flow in "Complete Workflow"
     * document for more detail.
     *
     * @return serialized Transaction
     * @throws TransactionSerializeError thrown if there are any exceptions while serializing
     * transaction. Cause: {@link TransactionCreateSignatureRequestError}
     *      <br>
     *          - Exception while trying to clone transaction for runtime handling.
     *          Method used {@link Utils#clone(Serializable)}
     *      <br>
     *      Thrown as parent error class for:
     *      <br>
     *          - {@link TransactionCreateSignatureRequestRpcError}, which is thrown if any Rpc call
     *          ({@link IRPCProvider#getInfo()}) causes an exception
     *      <br>
     *          - {@link TransactionCreateSignatureRequestAbiError} thrown if any error occurs while
     *          calling {@link IABIProvider#getAbi(String, EOSIOName)} to get the ABI needed to
     *          serialize each action
     *      <br>
     *          - {@link TransactionCreateSignatureRequestSerializationError}, which is thrown if any
     *          error happens while calling
     *          {@link ISerializationProvider#serialize(AbiEosSerializationObject)} to serialize
     *          each action or calling {@link ISerializationProvider#serializeTransaction(String)}
     *          to serialize the whole transaction.
     */
    @Nullable
    public String serialize() throws TransactionSerializeError {
        System.out.println("Got in to serialize()");
        // Return serialized version of the Transaction, if it exists, otherwise serialize the
        // transaction and return the result.
        if (this.serializedTransaction != null && !this.serializedTransaction.isEmpty()) {
            return this.serializedTransaction;
        }

        try {
            return this.serializeTransaction();
        } catch (TransactionCreateSignatureRequestError transactionCreateSignatureRequestError) {
            throw new TransactionSerializeError(
                    ErrorConstants.TRANSACTION_PROCESSOR_SERIALIZE_ERROR,
                    transactionCreateSignatureRequestError);
        }
    }

    //endregion

    //region private methods

    /**
     * Create signature request which will be sent to signature provider to be signed.
     * <p>
     * Check createSignatureRequest() flow in "Complete Workflow" document for more details.
     */
    @NotNull
    private EosioTransactionSignatureRequest createSignatureRequest()
            throws TransactionCreateSignatureRequestError {
        if (this.transaction == null) {
            throw new TransactionCreateSignatureRequestError(
                    ErrorConstants.TRANSACTION_PROCESSOR_TRANSACTION_HAS_TO_BE_INITIALIZED);
        }

        if (this.transaction.getActions().isEmpty()) {
            throw new TransactionCreateSignatureRequestError(
                    ErrorConstants.TRANSACTION_PROCESSOR_ACTIONS_EMPTY_ERROR_MSG);
        }

        // Cache the serialized version of transaction in the TransactionProcessor
        this.serializedTransaction = this.serializeTransaction();

        EosioTransactionSignatureRequest eosioTransactionSignatureRequest = new EosioTransactionSignatureRequest(
                this.serializedTransaction,
                null,
                this.chainId,
                null,
                this.isTransactionModificationAllowed);

        // Assign required keys to signing public keys if it was set.
        if (this.requiredKeys != null && !this.requiredKeys.isEmpty()) {
            eosioTransactionSignatureRequest.setSigningPublicKeys(this.requiredKeys);
            return eosioTransactionSignatureRequest;
        }

        // Getting required keys
        // 1.Getting available keys
        if (this.availableKeys == null || this.availableKeys.isEmpty()) {
            try {
                this.availableKeys = this.signatureProvider.getAvailableKeys();
            } catch (GetAvailableKeysError getAvailableKeysError) {
                throw new TransactionCreateSignatureRequestKeyError(
                        ErrorConstants.TRANSACTION_PROCESSOR_GET_AVAILABLE_KEY_ERROR,
                        getAvailableKeysError);
            }

            if (this.availableKeys.isEmpty()) {
                throw new TransactionCreateSignatureRequestEmptyAvailableKeyError(
                        ErrorConstants.TRANSACTION_PROCESSOR_GET_AVAILABLE_KEY_EMPTY);
            }
        }

        // 2.Getting required keys by getRequiredKeys() RPC call
        try {
            GetRequiredKeysResponse getRequiredKeysResponse = this.rpcProvider
                    .getRequiredKeys(
                            new GetRequiredKeysRequest(
                                    this.availableKeys,
                                    this.transaction));
            if (getRequiredKeysResponse.getRequiredKeys() == null || getRequiredKeysResponse
                    .getRequiredKeys().isEmpty()) {
                throw new TransactionCreateSignatureRequestRequiredKeysEmptyError(
                        ErrorConstants.GET_REQUIRED_KEY_RPC_EMPTY_RESULT);
            }

            List<String> backendRequiredKeys = getRequiredKeysResponse.getRequiredKeys();
            if (!this.availableKeys.containsAll(backendRequiredKeys)) {
                throw new TransactionCreateSignatureRequestRequiredKeysEmptyError(
                        ErrorConstants.TRANSACTION_PROCESSOR_REQUIRED_KEY_NOT_SUBSET);
            }

            this.requiredKeys = backendRequiredKeys;
        } catch (GetRequiredKeysRpcError getRequiredKeysRpcError) {
            throw new TransactionCreateSignatureRequestRpcError(
                    ErrorConstants.TRANSACTION_PROCESSOR_RPC_GET_REQUIRED_KEYS,
                    getRequiredKeysRpcError);
        }

        eosioTransactionSignatureRequest.setSigningPublicKeys(this.requiredKeys);
        return eosioTransactionSignatureRequest;
    }

    /**
     * Passing {@link EosioTransactionSignatureRequest} to signature provider for signing.
     * <p>
     * Check getSignature() flow in "Complete Workflow" documents for more details.
     *
     * @param eosioTransactionSignatureRequest The request for signature
     * @return Response from Signature provider
     */
    @NotNull
    private EosioTransactionSignatureResponse getSignature(
            EosioTransactionSignatureRequest eosioTransactionSignatureRequest)
            throws TransactionGetSignatureError {
        EosioTransactionSignatureResponse eosioTransactionSignatureResponse;
        try {
            eosioTransactionSignatureResponse = this.signatureProvider
                    .signTransaction(eosioTransactionSignatureRequest);
            if (eosioTransactionSignatureResponse.getError() != null) {
                throw eosioTransactionSignatureResponse.getError();
            }
        } catch (SignatureProviderError signatureProviderError) {
            throw new TransactionGetSignatureSigningError(
                    ErrorConstants.TRANSACTION_PROCESSOR_SIGN_TRANSACTION_ERROR,
                    signatureProviderError);
        }

        if (Strings.isNullOrEmpty(eosioTransactionSignatureResponse.getSerializeTransaction())) {
            throw new TransactionGetSignatureSigningError(
                    ErrorConstants.TRANSACTION_PROCESSOR_SIGN_TRANSACTION_TRANS_EMPTY_ERROR);
        }

        if (eosioTransactionSignatureResponse.getSignatures().isEmpty()) {
            throw new TransactionGetSignatureSigningError(
                    ErrorConstants.TRANSACTION_PROCESSOR_SIGN_TRANSACTION_SIGN_EMPTY_ERROR);
        }

        // Store current transaction as original transaction
        this.originalTransaction = this.transaction;

//        if (this.serializedTransaction != null
//                && !this.serializedTransaction
//                .equals(eosioTransactionSignatureResponse.getSerializeTransaction())) {
//            // Throw error if an unmodifiable transaction is modified
//            if (!this.isTransactionModificationAllowed) {
//                throw new TransactionGetSignatureNotAllowModifyTransactionError(
//                        ErrorConstants.TRANSACTION_IS_NOT_ALLOWED_TOBE_MODIFIED);
//            }
//
//            /* Deserialize and update new transaction to the current transaction if it was
//               and modification is allowed.*/
//            String transactionJSON;
//            try {
//                transactionJSON = this.serializationProvider
//                        .deserializeTransaction(
//                                eosioTransactionSignatureResponse.getSerializeTransaction());
//                if (transactionJSON == null || transactionJSON.isEmpty()) {
//                    throw new DeserializeTransactionError(
//                            ErrorConstants.TRANSACTION_PROCESSOR_GET_SIGN_DESERIALIZE_TRANS_EMPTY_ERROR);
//                }
//            } catch (DeserializeTransactionError deserializeTransactionError) {
//                throw new TransactionGetSignatureDeserializationError(
//                        ErrorConstants.TRANSACTION_PROCESSOR_GET_SIGN_DESERIALIZE_TRANS_ERROR,
//                        deserializeTransactionError);
//            }
//
//            this.transaction = Utils.getGson(DateFormatter.BACKEND_DATE_PATTERN).fromJson(transactionJSON, TransactionTest.class);
//        }

        this.signatures = new ArrayList<>();
        this.signatures.addAll(eosioTransactionSignatureResponse.getSignatures());
        this.serializedTransaction = eosioTransactionSignatureResponse.getSerializeTransaction();
        return eosioTransactionSignatureResponse;
    }

    /**
     * Push signed transaction to blockchain.
     * <p>
     * Check pushTransaction() flow in "Complete Workflow" document for more details.
     *
     * @param pushTransactionRequest the request
     * @return Response from chain
     */
    @NotNull
    private PushTransactionResponse pushTransaction(PushTransactionRequest pushTransactionRequest)
            throws TransactionPushTransactionError {
        try {
            return this.rpcProvider.pushTransaction(pushTransactionRequest);
        } catch (PushTransactionRpcError pushTransactionRpcError) {
            throw new TransactionPushTransactionError(
                    ErrorConstants.TRANSACTION_PROCESSOR_RPC_PUSH_TRANSACTION,
                    pushTransactionRpcError);
        }
    }

    /**
     * Serialize current transaction
     *
     * @return serialized transaction in Hex
     * @throws TransactionCreateSignatureRequestError thrown if there are any exceptions while serializing transaction:
     *      <br>
     *          - Exception while cloning transaction for runtime handling. Method used {@link Utils#clone(Serializable)}
     *      <br>
     *      Thrown as parent error class for:
     *      <br>
     *          - {@link TransactionCreateSignatureRequestRpcError}, which is thrown if any RPC call
     *          ({@link IRPCProvider#getInfo()}) results in an exception
     *      <br>
     *          - {@link TransactionCreateSignatureRequestAbiError}, which is thrown if any error
     *          occurs while calling {@link IABIProvider#getAbi(String, EOSIOName)} to get the ABI
     *          needed to serialize an action
     *      <br>
     *          - {@link TransactionCreateSignatureRequestSerializationError}, which is thrown if
     *          an exception occurs while calling
     *          {@link ISerializationProvider#serialize(AbiEosSerializationObject)} to serialize each action
     *          or calling {@link ISerializationProvider#serializeTransaction(String)} to serialize the whole transaction.
     */
    @NotNull
    private String serializeTransaction() throws TransactionCreateSignatureRequestError {
        System.out.println("Got in to serializeTransaction");
        TransactionTest clonedTransaction;
        try {
            clonedTransaction = this.getDeepClone();
        } catch (IOException e) {
            throw new TransactionCreateSignatureRequestError(
                    ErrorConstants.TRANSACTION_PROCESSOR_PREPARE_CLONE_ERROR, e);
        } catch (ClassNotFoundException e) {
            throw new TransactionCreateSignatureRequestError(
                    ErrorConstants.TRANSACTION_PROCESSOR_PREPARE_CLONE_CLASS_NOT_FOUND, e);
        }

        if (clonedTransaction == null) {
            throw new TransactionCreateSignatureRequestError(
                    ErrorConstants.TRANSACTION_PROCESSOR_PREPARE_CLONE_ERROR);
        }

        /* Check for chain id
         Call getInfo() if chainId is NULL or Empty
         */
        if (this.chainId == null || this.chainId.isEmpty()) {
            try {
                GetInfoResponse getInfoResponse = this.rpcProvider.getInfo();
                this.chainId = getInfoResponse.getChainId();
            } catch (GetInfoRpcError getInfoRpcError) {
                throw new TransactionCreateSignatureRequestRpcError(
                        ErrorConstants.TRANSACTION_PROCESSOR_RPC_GET_INFO, getInfoRpcError);
            }
        }

        // Serialize each action of Transaction's actions
        for (Action action : clonedTransaction.getActions()) {
            AbiEosSerializationObject actionAbiEosSerializationObject = this.serializeAction(action, this.chainId, this.abiProvider);
            // !!! Set serialization result to data field of the action
            action.setData(actionAbiEosSerializationObject.getHex());
        }

        // Serialize each action of Transaction's context free actions if they exist.
        if (!clonedTransaction.getContextFreeActions().isEmpty()) {
            for (Action contextFreeAction : clonedTransaction.getContextFreeActions()) {
                AbiEosSerializationObject actionAbiEosSerializationObject = this.serializeAction(contextFreeAction, this.chainId, this.abiProvider);
                // !!! Set serialization result to data field of the contextFreeAction
                contextFreeAction.setData(actionAbiEosSerializationObject.getHex());
            }
        }

//        AbiEosSerializationObject contextFreeDataSerializationObject =
//                this.serializeContextFreeData(clonedTransaction.getContextFreeData(), this.chainId, this.abiProvider);
//
//        clonedTransaction.setContextFreeData(contextFreeDataSerializationObject.getHex());

        // Apply serialized actions to current transaction to be used on getRequiredKeys
        // From now, the current transaction keep serialized actions
        this.transaction = clonedTransaction;

        // Serialize the whole transaction
        String _serializedTransaction;
        try {
            String clonedTransactionToJSON = Utils.getGson(DateFormatter.BACKEND_DATE_PATTERN).toJson(clonedTransaction);
            System.out.println("JSON before: " + clonedTransactionToJSON);
            _serializedTransaction = this.serializationProvider.serializeTransaction(clonedTransactionToJSON);
            if (_serializedTransaction == null || _serializedTransaction.isEmpty()) {
                throw new TransactionCreateSignatureRequestSerializationError(
                        ErrorConstants.TRANSACTION_PROCESSOR_SERIALIZE_TRANSACTION_WORKED_BUT_EMPTY_RESULT);
            }

        } catch (SerializeTransactionError serializeTransactionError) {
            throw new TransactionCreateSignatureRequestSerializationError(serializeTransactionError.getMessage());
        }

//        String deserialized = "";
//        try {
//            deserialized = this.deserializeTransactionTest(_serializedTransaction);
//        } catch (DeserializeTransactionError deserializeTransactionError) {
//            deserializeTransactionError.printStackTrace();
//        }
//        System.out.println("Deserialized: " + deserialized);

        return _serializedTransaction;
    }

    public String serializeTransactionTest(String json) throws SerializeTransactionError {
        String abi = "{\n" +
                "    \"version\": \"eosio::abi/1.0\",\n" +
                "    \"types\": [\n" +
                "        {\n" +
                "            \"new_type_name\": \"account_name\",\n" +
                "            \"type\": \"name\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"new_type_name\": \"action_name\",\n" +
                "            \"type\": \"name\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"new_type_name\": \"permission_name\",\n" +
                "            \"type\": \"name\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"structs\": [\n" +
                "        {\n" +
                "            \"name\": \"permission_level\",\n" +
                "            \"base\": \"\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"name\": \"actor\",\n" +
                "                    \"type\": \"account_name\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"permission\",\n" +
                "                    \"type\": \"permission_name\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"action\",\n" +
                "            \"base\": \"\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"name\": \"account\",\n" +
                "                    \"type\": \"account_name\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"name\",\n" +
                "                    \"type\": \"action_name\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"authorization\",\n" +
                "                    \"type\": \"permission_level[]\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"data\",\n" +
                "                    \"type\": \"bytes\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"extension\",\n" +
                "            \"base\": \"\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"name\": \"type\",\n" +
                "                    \"type\": \"uint16\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"data\",\n" +
                "                    \"type\": \"bytes\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"transaction_header\",\n" +
                "            \"base\": \"\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"name\": \"expiration\",\n" +
                "                    \"type\": \"time_point_sec\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"ref_block_num\",\n" +
                "                    \"type\": \"uint16\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"ref_block_prefix\",\n" +
                "                    \"type\": \"uint32\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"max_net_usage_words\",\n" +
                "                    \"type\": \"varuint32\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"max_cpu_usage_ms\",\n" +
                "                    \"type\": \"uint8\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"delay_sec\",\n" +
                "                    \"type\": \"varuint32\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"transaction\",\n" +
                "            \"base\": \"transaction_header\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"name\": \"context_free_actions\",\n" +
                "                    \"type\": \"action[]\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"actions\",\n" +
                "                    \"type\": \"action[]\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"transaction_extensions\",\n" +
                "                    \"type\": \"extension[]\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"context_free_data\",\n" +
                "                    \"type\": \"bytes\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        try {
            AbiEosSerializationObject serializationObject = new AbiEosSerializationObject(null,
                    "", "transaction", abi);
            serializationObject.setJson(json);
            this.serializationProvider.serialize(serializationObject);
            return serializationObject.getHex();
        } catch (SerializationProviderError serializationProviderError) {
            throw new SerializeTransactionError(serializationProviderError);
        }
    }

    public String deserializeTransactionTest(String hex) throws DeserializeTransactionError {
        String abi = "{\n" +
                "    \"version\": \"eosio::abi/1.0\",\n" +
                "    \"types\": [\n" +
                "        {\n" +
                "            \"new_type_name\": \"account_name\",\n" +
                "            \"type\": \"name\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"new_type_name\": \"action_name\",\n" +
                "            \"type\": \"name\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"new_type_name\": \"permission_name\",\n" +
                "            \"type\": \"name\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"structs\": [\n" +
                "        {\n" +
                "            \"name\": \"permission_level\",\n" +
                "            \"base\": \"\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"name\": \"actor\",\n" +
                "                    \"type\": \"account_name\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"permission\",\n" +
                "                    \"type\": \"permission_name\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"action\",\n" +
                "            \"base\": \"\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"name\": \"account\",\n" +
                "                    \"type\": \"account_name\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"name\",\n" +
                "                    \"type\": \"action_name\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"authorization\",\n" +
                "                    \"type\": \"permission_level[]\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"data\",\n" +
                "                    \"type\": \"bytes\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"extension\",\n" +
                "            \"base\": \"\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"name\": \"type\",\n" +
                "                    \"type\": \"uint16\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"data\",\n" +
                "                    \"type\": \"bytes\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"transaction_header\",\n" +
                "            \"base\": \"\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"name\": \"expiration\",\n" +
                "                    \"type\": \"time_point_sec\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"ref_block_num\",\n" +
                "                    \"type\": \"uint16\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"ref_block_prefix\",\n" +
                "                    \"type\": \"uint32\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"max_net_usage_words\",\n" +
                "                    \"type\": \"varuint32\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"max_cpu_usage_ms\",\n" +
                "                    \"type\": \"uint8\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"delay_sec\",\n" +
                "                    \"type\": \"varuint32\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"transaction\",\n" +
                "            \"base\": \"transaction_header\",\n" +
                "            \"fields\": [\n" +
                "                {\n" +
                "                    \"name\": \"context_free_actions\",\n" +
                "                    \"type\": \"action[]\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"actions\",\n" +
                "                    \"type\": \"action[]\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"transaction_extensions\",\n" +
                "                    \"type\": \"extension[]\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"context_free_data\",\n" +
                "                    \"type\": \"bytes\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        try {
            AbiEosSerializationObject serializationObject = new AbiEosSerializationObject(null,
                    "", "transaction", abi);
            serializationObject.setHex(hex);
            this.serializationProvider.deserialize(serializationObject);
            return serializationObject.getJson();
        } catch (SerializationProviderError serializationProviderError) {
            throw new DeserializeTransactionError(serializationProviderError);
        }
    }

    /**
     * Serializing an action's JSON data to Hex format by using {@link IABIProvider} and {@link ISerializationProvider}
     *
     * @param action - input action to serialize.
     * @param chainId - the chain id.
     * @param abiProvider - an instance of ABI provider.
     * @return A serialized object from {@link ISerializationProvider} which contains the hex format of the action's JSON data.
     * @throws TransactionCreateSignatureRequestError thrown if there are any exceptions while serializing transaction:
     *      <br>
     *          - {@link TransactionCreateSignatureRequestAbiError}, which is thrown if any error
     *          occurs while calling {@link IABIProvider#getAbi(String, EOSIOName)} to get the ABI
     *          needed to serialize an action
     *      <br>
     *          - {@link TransactionCreateSignatureRequestSerializationError}, which is thrown if
     *          an exception occurs while calling
     *          {@link ISerializationProvider#serialize(AbiEosSerializationObject)} to serialize each action
     *          or calling {@link ISerializationProvider#serializeTransaction(String)} to serialize the whole transaction.
     */
    @NotNull
    private AbiEosSerializationObject serializeAction(Action action, String chainId, IABIProvider abiProvider)
            throws TransactionCreateSignatureRequestError {
        String actionAbiJSON;
        try {
            actionAbiJSON = abiProvider
                    .getAbi(chainId, new EOSIOName(action.getAccount()));
        } catch (GetAbiError getAbiError) {
            throw new TransactionCreateSignatureRequestAbiError(
                    String.format(ErrorConstants.TRANSACTION_PROCESSOR_GET_ABI_ERROR,
                            action.getAccount()), getAbiError);
        }

        AbiEosSerializationObject actionAbiEosSerializationObject = new AbiEosSerializationObject(
                action.getAccount(), action.getName(),
                null, actionAbiJSON);
        actionAbiEosSerializationObject.setHex("");

        // !!! At this step, the data field of the action is still in JSON format.
        actionAbiEosSerializationObject.setJson(action.getData());

        try {
            this.serializationProvider.serialize(actionAbiEosSerializationObject);
            if (actionAbiEosSerializationObject.getHex().isEmpty()) {
                throw new TransactionCreateSignatureRequestSerializationError(
                        ErrorConstants.TRANSACTION_PROCESSOR_SERIALIZE_ACTION_WORKED_BUT_EMPTY_RESULT);
            }
        } catch (SerializeError | TransactionCreateSignatureRequestSerializationError serializeError) {
            throw new TransactionCreateSignatureRequestSerializationError(
                    String.format(ErrorConstants.TRANSACTION_PROCESSOR_SERIALIZE_ACTION_ERROR,
                            action.getAccount()), serializeError);
        }

        return actionAbiEosSerializationObject;
    }

    @NotNull
    private AbiEosSerializationObject serializeContextFreeData(String contextFreeData, String chainId, IABIProvider abiProvider)
            throws TransactionCreateSignatureRequestError {
        String actionAbiJSON;
        try {
            actionAbiJSON = abiProvider
                    .getAbi(chainId, new EOSIOName("tictactoe"));
        } catch (GetAbiError getAbiError) {
            throw new TransactionCreateSignatureRequestAbiError(
                    String.format(ErrorConstants.TRANSACTION_PROCESSOR_GET_ABI_ERROR,
                            "tictactoe"), getAbiError);
        }

        AbiEosSerializationObject actionAbiEosSerializationObject = new AbiEosSerializationObject(
                "tictactoe", "contextfree",
                null, actionAbiJSON);
        actionAbiEosSerializationObject.setHex("");

        // !!! At this step, the data field of the action is still in JSON format.
        actionAbiEosSerializationObject.setJson(contextFreeData);

        try {
            this.serializationProvider.serialize(actionAbiEosSerializationObject);
            if (actionAbiEosSerializationObject.getHex().isEmpty()) {
                throw new TransactionCreateSignatureRequestSerializationError(
                        ErrorConstants.TRANSACTION_PROCESSOR_SERIALIZE_ACTION_WORKED_BUT_EMPTY_RESULT);
            }
        } catch (SerializeError | TransactionCreateSignatureRequestSerializationError serializeError) {
            throw new TransactionCreateSignatureRequestSerializationError(
                    String.format(ErrorConstants.TRANSACTION_PROCESSOR_SERIALIZE_ACTION_ERROR,
                            "tictactoe"), serializeError);
        }

        return actionAbiEosSerializationObject;
    }

    /**
     * Getting deep clone of the transaction
     */
    private TransactionTest getDeepClone() throws IOException, ClassNotFoundException {
        if (this.transaction == null) {
            return null;
        }

        return Utils.clone(this.transaction);
    }

    /**
     * Called when prepare() is finished
     *
     * @param preparingTransaction - prepared transaction
     */
    private void finishPreparing(TransactionTest preparingTransaction) {
        this.transaction = preparingTransaction;
        // Clear serialized transaction if it was serialized.
        if (!Strings.isNullOrEmpty(this.serializedTransaction)) {
            this.serializedTransaction = "";
        }
    }

    //endregion

    //region getters/setters

    /**
     * Gets transaction instance which holds all data relating to EOS Transaction
     * <p>
     * This object holds the non-serialized version of Transaction
     * @return the current transaction
     */
    @Nullable
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Gets Transaction instance that holds the original transaction reference after signature provider
     * returns the signing result.
     *
     * Check getSignature() flow in "Complete Workflow" document for more details.
     * @return the original transaction
     */
    @Nullable
    public Transaction getOriginalTransaction() {
        return originalTransaction;
    }

    /**
     * List of signatures that will be available after they are returned from the signature
     * provider.
     * <p>
     * Check getSignature() flow in "Complete Workflow" document for more details.
     * @return list of EOS format signature.
     */
    @NotNull
    public List<String> getSignatures() {
        return signatures;
    }

    /**
     * Gets serialized version of Transaction.  This is stored to later determine whether the
     * signature provider modified the transaction.
     * <p>
     *     If the transaction has been modified, this object needs to be cleared or updated.
     * <p>
     *     Check getSignature() flow in "Complete Workflow" document for more details.
     * @return the serialized transaction.
     */
    @Nullable
    public String getSerializedTransaction() {
        return serializedTransaction;
    }

    /**
     * Gets configuration for Transaction which offers ability to set:
     * <p>
     * - The expiration period for the transaction in seconds
     * <p>
     * - How many blocks behind
     * @return the configuration for transaction
     */
    @NotNull
    public TransactionConfig getTransactionConfig() {
        return transactionConfig;
    }

    /**
     * Sets configuration for Transaction which offers ability to set:
     * <p>
     * - The expiration period for the transaction in seconds
     * <p>
     * - How many blocks behind
     * @param transactionConfig the input configuration for transaction
     */
    public void setTransactionConfig(@NotNull TransactionConfig transactionConfig) {
        this.transactionConfig = transactionConfig;
    }

    /**
     * Sets chain id value. If the value has not been set yet, the getInfo() RPC call will be used
     * to get it.
     * @param chainId - input chain id
     */
    public void setChainId(@Nullable String chainId) {
        this.chainId = chainId;
    }

    /**
     * Set value for available keys list.
     * <p>
     * List of available keys which most likely come from signature provider.
     * <p>
     * If this list is set, TransactionProcessor won't ask for available keys from the signature
     * provider and will use this list.
     * <p>
     * Check createSignatureRequest() flow in "Complete Workflow" document for more details.
     *
     * @param availableKeys the input available keys
     */
    public void setAvailableKeys(@NotNull List<String> availableKeys) {
        this.availableKeys = availableKeys;
    }

    /**
     * Set value for required keys list
     * <p>
     * List of required keys to sign the transaction.
     * <p>
     * If this list is set, TransactionProcessor won't make RPC call for getRequiredKeys().
     * <p>
     * Check createSignatureRequest() flow in "Complete Workflow" document for more details.
     *
     * @param requiredKeys the input required keys
     */
    public void setRequiredKeys(@NotNull List<String> requiredKeys) {
        this.requiredKeys = requiredKeys;
    }

    /**
     * Should the signature provider be able to modify the transaction?
     * @return Whether to allow transaction to be modified by Signature Provider.
     */
    public boolean isTransactionModificationAllowed() {
        return isTransactionModificationAllowed;
    }

    /**
     * True: The transaction could be modified and updated to current transaction by Signature
     * provider.
     * <p>
     * False: No modification. {@link TransactionGetSignatureNotAllowModifyTransactionError} will be
     * thrown if transaction is modified.
     * @param isTransactionModificationAllowed Whether allow transaction to be modified by Signature Provider.
     */
    public void setIsTransactionModificationAllowed(boolean isTransactionModificationAllowed) {
        this.isTransactionModificationAllowed = isTransactionModificationAllowed;
    }

    //endregion
}

