package dev.klepto.unreflect.benchmark;

import lombok.val;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public class Benchmarks {

    public static void main(String[] args) throws RunnerException {
        val options = new OptionsBuilder()
                .include(ConstructorBenchmarks.class.getSimpleName())
                .include(MethodBenchmarks.class.getSimpleName())
                .include(FieldBenchmarks.class.getSimpleName())
                .build();
        
        new Runner(options).run();
    }

}
