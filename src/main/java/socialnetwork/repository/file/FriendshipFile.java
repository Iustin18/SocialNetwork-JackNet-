/*
package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long,Long>, Friendship> {
    public FriendshipFile(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {
        Long id1 = Long.parseLong(attributes.get(0));
        Long id2 = Long.parseLong(attributes.get(1));
        return new Friendship(new Tuple<>(id1,id2));
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getLeft()+";"+entity.getId().getRight();
    }
}
*/
