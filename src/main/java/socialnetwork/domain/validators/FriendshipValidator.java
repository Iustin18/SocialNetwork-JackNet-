package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship>{
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getPair().getRight().equals(entity.getPair().getLeft())){
            throw new ValidationException("Id users must be different/");
        }
    }

    /**
     *
     * @param id1 - id that have to be validated
     * @return the Long value of the id
     */
    public static Long validateId(String id1){
        try{
            return Long.parseLong(id1);
        } catch (NumberFormatException e) {
            throw new ValidationException(id1 + " must be a number/");
        }
    }
}
