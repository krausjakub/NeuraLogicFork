package constructs.template.metadata;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by gusta on 5.3.18.
 */
public class PredicateMetadata extends Metadata {
    private static final Logger LOG = Logger.getLogger(PredicateMetadata.class.getName());

    public PredicateMetadata(Map<String, Object> pairs){
        super(pairs);
    }

    @Override
    public boolean addValidateMetadatum(String parameter, Object Value) {
        //TODO check for all valid combinations of parameter types and corresponding value types

        return false;
    }
}