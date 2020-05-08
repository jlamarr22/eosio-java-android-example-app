package one.block.androidexampleapp.testImplementation.serialization;

import java.util.ArrayList;
import java.util.List;

import one.block.androidexampleapp.testImplementation.DeserializeContextFreeDataError;
import one.block.androidexampleapp.testImplementation.SerializeContextFreeDataError;
import one.block.androidexampleapp.testImplementation.serialization.ISerializationProviderTest;
import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojavaabieosserializationprovider.AbiEosSerializationProviderImpl;

public class SerializationProviderImplTest extends AbiEosSerializationProviderImpl implements ISerializationProviderTest {
    /**
     * Create a new AbiEosSerializationProviderImpl serialization provider instance, initilizing a new context for the C++
     * library to work on automatically.
     *
     * @throws SerializationProviderError - An error is thrown if the context cannot be created.
     */
    public SerializationProviderImplTest() throws SerializationProviderError {
    }

    @Override
    public List<String> deserializeContextFreeData(String hex) throws DeserializeContextFreeDataError {
        return new ArrayList<String>();
    }

    @Override
    public String serializeContextFreeData(List<String> contextFreeData) throws SerializeContextFreeDataError {
        return "";
    }
}
