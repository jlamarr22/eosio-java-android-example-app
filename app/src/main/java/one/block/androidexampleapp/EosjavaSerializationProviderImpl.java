package one.block.androidexampleapp;

import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojavaabieosserializationprovider.AbiEosSerializationProviderImpl;

public class EosjavaSerializationProviderImpl extends AbiEosSerializationProviderImpl {
    /**
     * Create a new AbiEosSerializationProviderImpl serialization provider instance, initilizing a new context for the C++
     * library to work on automatically.
     *
     * @throws SerializationProviderError - An error is thrown if the context cannot be created.
     */
    public EosjavaSerializationProviderImpl() throws SerializationProviderError {
        super();
    }
}
