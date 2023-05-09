package dev.klepto.unreflect.benchmark;

import dev.klepto.unreflect.FieldAccess;
import dev.klepto.unreflect.Unreflect;
import dev.klepto.unreflect.property.Mutable;
import lombok.SneakyThrows;
import lombok.val;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
public class FieldBenchmarks {

    private final Subject subject;

    private final Mutable direct;
    private final Mutable reflect;
    private final Mutable unreflect;

    @SneakyThrows
    public FieldBenchmarks() {
        this.subject = new Subject();

        this.direct = new Mutable() {
            @Override
            public <T> T get() {
                return (T) (Object) subject.value;
            }

            @Override
            public void set(Object value) {
                subject.value = (int) value;
            }
        };

        this.reflect = Unreflect.reflect(subject).field("value");
        this.unreflect = Unreflect.unreflect(subject).field("value");
    }

    @Benchmark
    public void direct() {
        val value = (int) direct.get();
        direct.set(value + 1);
    }

    @Benchmark
    @SneakyThrows
    public void reflection() {
        val value = (int) reflect.get();
        reflect.set(value + 1);
    }


    @Benchmark
    public void unreflect() {
        val value = (int) unreflect.get();
        unreflect.set(value + 1);
    }

    private static class Subject {
        private int value = 0;
    }

}
