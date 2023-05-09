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
public class MethodBenchmarks {

    private final Subject subject;
    private final Invokable direct;
    private final Invokable reflect;
    private final Invokable unreflect;

    @SneakyThrows
    public MethodBenchmarks() {
        this.subject = new Subject();
        this.direct = new Invokable() {
            @Override
            public StreamEx<ParameterAccess> parameters() {
                return null;
            }

            @Override
            public <T> T invoke(Object... args) throws RuntimeException {
                subject.increaseValue((int) args[0]);
                return null;
            }
        };
        this.reflect = Unreflect.reflect(subject).method(0);
        this.unreflect = Unreflect.unreflect(subject).method(0);
    }

    @Benchmark
    public void direct() {
        direct.invoke(1);
    }

    @Benchmark
    @SneakyThrows
    public void reflection() {
        reflect.invoke(1);
    }

    @Benchmark
    public void unreflect() {
        unreflect.invoke(1);
    }

    private static class Subject {
        private int value = 0;
        public void increaseValue(int amount) {
            value += amount;
        }
    }

}
