package socialnetwork.domain.validators;

public interface Validator<T> {

    /**
     * Validate an entity
     * @param entity - the entity to be validated
     * @throws ValidationException
     *          - If an entity is not valid
     */
    void validate(T entity) throws ValidationException;
}