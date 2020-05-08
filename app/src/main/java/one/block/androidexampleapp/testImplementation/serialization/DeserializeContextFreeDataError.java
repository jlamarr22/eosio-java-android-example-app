package one.block.androidexampleapp.testImplementation.serialization;

import org.jetbrains.annotations.NotNull;

import one.block.eosiojava.error.serializationProvider.SerializationProviderError;

public class DeserializeContextFreeDataError extends SerializationProviderError {

    public DeserializeContextFreeDataError() {
    }

    public DeserializeContextFreeDataError(@NotNull String message) {
        super(message);
    }

    public DeserializeContextFreeDataError(@NotNull String message,
                                         @NotNull Exception exception) {
        super(message, exception);
    }

    public DeserializeContextFreeDataError(@NotNull Exception exception) {
        super(exception);
    }
}
