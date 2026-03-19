package services;

public interface Storable {
    /**
     * Converts the object data into a single string line for text file storage.
     */
    String toFileFormat();
}