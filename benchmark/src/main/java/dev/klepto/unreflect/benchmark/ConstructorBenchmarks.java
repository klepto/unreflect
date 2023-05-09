package dev.klepto.unreflect.benchmark;

import dev.klepto.unreflect.ParameterAccess;
import dev.klepto.unreflect.Unreflect;
import dev.klepto.unreflect.property.Invokable;
import lombok.SneakyThrows;
import one.util.streamex.StreamEx;
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
public class ConstructorBenchmarks {

    private final Invokable direct;
    private final Invokable reflect;
    private final Invokable unreflect;

    @SneakyThrows
    public ConstructorBenchmarks() {
        this.direct = new Invokable() {
            @Override
            public StreamEx<ParameterAccess> parameters() {
                return null;
            }

            @Override
            public <T> T invoke(Object... args) throws RuntimeException {
                return (T) new Subject();
            }
        };
        this.reflect = Unreflect.reflect(Subject.class).constructor();
        this.unreflect = Unreflect.unreflect(Subject.class).constructor();
    }

    @Benchmark
    public void direct() {
        direct.invoke();
    }

    @Benchmark
    @SneakyThrows
    public void reflection() {
        reflect.invoke();
    }

    @Benchmark
    public void unreflect() {
        unreflect.invoke();
    }

    private static class Subject {
        public Subject() {
        }
    }

}
