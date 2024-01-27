package dev.klepto.unreflect.util;

import dev.klepto.unreflect.ParameterAccess;
import dev.klepto.unreflect.UnreflectType;
import dev.klepto.unreflect.property.Invokable;
import lombok.val;
import one.util.streamex.StreamEx;

import java.util.Collection;

/**
 * Utility functions for parameter matching.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
public class Parameters {

    private Parameters() {
    }

    /**
     * Matches parameter arguments or parameter types with given invokable. Used for ability to lookup constructors and
     * methods by using either values or value types.
     *
     * @param invokable   the invokable
     * @param args an array of parameter values or parameter types
     * @return true if invokable parameters match given parameter values or types
     */
    public static boolean matches(Invokable invokable, Object[] args) {
        val parameters = invokable.parameters().toList();
        if (parameters.size() != args.length) {
            return false;
        }

        for (int i = 0; i < parameters.size(); i++) {
            val parameterType = parameters.get(i).type();
            val argType = UnreflectType.of(args[i]);
            if (!argType.matches(parameterType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts collection of parameters into easy to read signature. Used for debugging methods and constructors.
     *
     * @param parameters a collection containing parameters
     * @return a parameter signature string
     */
    public static String toString(Collection<ParameterAccess> parameters) {
        val result = StreamEx.of(parameters).map(ParameterAccess::toString).joining(", ");
        return "(" + result + ")";
    }

}
