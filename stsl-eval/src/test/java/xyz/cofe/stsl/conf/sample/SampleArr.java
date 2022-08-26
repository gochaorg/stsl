package xyz.cofe.stsl.conf.sample;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SampleArr {
    List<SampleConfig1> list();
    Collection<SampleConfig1> coll();
    Iterable<SampleConfig1> iter();
    Optional<SampleConfig1> opt();
    List<Optional<SampleConfig1>> listOpt();
}
