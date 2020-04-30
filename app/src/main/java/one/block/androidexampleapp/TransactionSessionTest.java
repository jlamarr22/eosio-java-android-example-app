package one.block.androidexampleapp;

import one.block.eosiojava.error.session.TransactionProcessorConstructorInputError;
import one.block.eosiojava.interfaces.IABIProvider;
import one.block.eosiojava.interfaces.IRPCProvider;
import one.block.eosiojava.interfaces.ISerializationProvider;
import one.block.eosiojava.interfaces.ISignatureProvider;
import one.block.eosiojava.models.rpcProvider.Transaction;
import one.block.eosiojava.session.TransactionSession;

import org.jetbrains.annotations.NotNull;

/**
 * Transaction Session class has a factory role for creating {@link TransactionProcessorTest} object from providers instances
 */
public class TransactionSessionTest {

    /**
     * Serialization provider to be used as a reference on {@link TransactionProcessorTest} object
     * <br>
     *     Responsible for serialization/deserialization between JSON and Hex for communicate with EOSIO chain
     */
    @NotNull
    private ISerializationProvider serializationProvider;

    /**
     * Rpc provider to be used as a reference on {@link TransactionProcessorTest} object
     * <br>
     *     Responsible for communicate with EOSIO chain
     */
    @NotNull
    private IRPCProvider rpcProvider;

    /**
     * ABI Provider to be used as a reference on {@link TransactionProcessorTest} object
     * <br>
     *     Responsible for managing ABIs for serialization/deserialization
     */
    @NotNull
    private IABIProvider abiProvider;

    /**
     * Signature provider to be used as a reference on {@link TransactionProcessorTest} object
     * <br>
     *     Responsible for managing keys, create signature to make transaction to EOSIO chain
     */
    @NotNull
    private ISignatureProvider signatureProvider;

    /**
     * Initialize TransactionSession object which acts like a factory to create {@link TransactionProcessorTest} object from providers instances.
     *
     * @param serializationProvider serialization provider.
     * @param rpcProvider Rpc provider.
     * @param abiProvider ABI provider.
     * @param signatureProvider signature provider.
     */
    public TransactionSessionTest(
            @NotNull ISerializationProvider serializationProvider,
            @NotNull IRPCProvider rpcProvider, @NotNull IABIProvider abiProvider,
            @NotNull ISignatureProvider signatureProvider) {
        this.serializationProvider = serializationProvider;
        this.rpcProvider = rpcProvider;
        this.abiProvider = abiProvider;
        this.signatureProvider = signatureProvider;
    }

    /**
     * Create and return a new instance of TransactionProcessor
     *
     * @return new instance of TransactionProcessor
     */
    public TransactionProcessorTest getTransactionProcessor() {
        return new TransactionProcessorTest(this.serializationProvider, this.rpcProvider,
                this.abiProvider, this.signatureProvider);
    }

    /**
     * Create and return a new instance of TransactionProcessor with preset transaction
     *
     * @param transaction - preset transaction
     * @return new instance of TransactionProcessor
     * @throws TransactionProcessorConstructorInputError thrown if initializing {@link TransactionProcessorTest} get error.
     */
    public TransactionProcessorTest getTransactionProcessor(TransactionTest transaction) throws TransactionProcessorConstructorInputError {
        return new TransactionProcessorTest(this.serializationProvider, this.rpcProvider,
                this.abiProvider, this.signatureProvider, transaction);
    }

    //region getters

    /**
     * Get serialization provider to be used as a reference on {@link TransactionProcessorTest} object
     * <br>
     *     Responsible for serialization/deserialization between JSON and Hex for communicate with EOSIO chain
     * @return the serialization provider
     */
    @NotNull
    public ISerializationProvider getSerializationProvider() {
        return serializationProvider;
    }

    /**
     * Get rpc provider to be used as a reference on {@link TransactionProcessorTest} object
     * <br>
     *     Responsible for communicate with EOSIO chain
     * @return the rpc provider.
     */
    @NotNull
    public IRPCProvider getRpcProvider() {
        return rpcProvider;
    }

    /**
     * Get ABI Provider to be used as a reference on {@link TransactionProcessorTest} object
     * <br>
     *     Responsible for managing ABIs for serialization/deserialization
     * @return the rpc provider.
     */
    @NotNull
    public IABIProvider getAbiProvider() {
        return abiProvider;
    }

    /**
     * Get signature provider to be used as a reference on {@link TransactionProcessorTest} object
     * <br>
     *     Responsible for managing keys, create signature to make transaction to EOSIO chain
     * @return the signature provider.
     */
    @NotNull
    public ISignatureProvider getSignatureProvider() {
        return signatureProvider;
    }
    //endregion
}

