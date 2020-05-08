package one.block.androidexampleapp.testImplementation.serialization;

import java.util.List;

import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojava.interfaces.ISerializationProvider;
import org.jetbrains.annotations.NotNull;

public class SerializeContextFreeDataError extends SerializationProviderError {

    public SerializeContextFreeDataError() {
    }

    public SerializeContextFreeDataError(@NotNull String message) {
        super(message);
    }

    public SerializeContextFreeDataError(@NotNull String message,
                                         @NotNull Exception exception) {
        super(message, exception);
    }

    public SerializeContextFreeDataError(@NotNull Exception exception) {
        super(exception);
    }
}