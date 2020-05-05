package one.block.androidexampleapp.testImplementation;

import java.util.List;

import one.block.eosiojava.models.signatureProvider.BinaryAbi;
import one.block.eosiojava.models.signatureProvider.EosioTransactionSignatureRequest;

public class EosioTransactionSignatureRequestTest extends EosioTransactionSignatureRequest {
    public String contextFreeData;
    /**
     * Instantiates a new Eosio transaction signature request.
     *
     * @param serializedTransaction the serialized transaction
     * @param signingPublicKeys     the signing public keys
     * @param chainId               the chain id
     * @param abis                  the ABIs
     * @param isModifiable          boolean to indicate whether the signature provider is able to modify the
     */
    public EosioTransactionSignatureRequestTest(String serializedTransaction, List<String> signingPublicKeys, String chainId, List<BinaryAbi> abis, boolean isModifiable, String contextFreeData) {
        super(serializedTransaction, signingPublicKeys, chainId, abis, isModifiable);
        this.contextFreeData = contextFreeData;
    }

    public String getContextFreeData() {
        return this.contextFreeData;
    }
}
