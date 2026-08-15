package lod.irongoon.services.compatibility;

import legend.game.combat.spells.SpellEffectPlan;
import legend.game.inventory.SpellStats0c;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class SpellEffectPlans {
    private static final Method GET_EFFECT_PLANS = findMethod("getEffectPlans");
    private static final Method SET_EFFECT_PLANS = findMethod("setEffectPlans", List.class);
    private static final Method GET_EFFECT_PLAN = findMethod("getEffectPlan");
    private static final Method SET_EFFECT_PLAN = findMethod("setEffectPlan", SpellEffectPlan.class);
    private static final boolean MULTI_PLAN_API = GET_EFFECT_PLANS != null && SET_EFFECT_PLANS != null;

    static {
        final boolean supportsSingle = GET_EFFECT_PLAN != null && SET_EFFECT_PLAN != null;
        if(!MULTI_PLAN_API && !supportsSingle) {
            throw new IllegalStateException("Unsupported Severed Chains SpellStats0c effect-plan API");
        }
    }

    private SpellEffectPlans() { }

    public static List<SpellEffectPlan> get(final SpellStats0c spell) {
        if(MULTI_PLAN_API) {
            @SuppressWarnings("unchecked")
            final List<SpellEffectPlan> plans = (List<SpellEffectPlan>)invoke(GET_EFFECT_PLANS, spell);
            return List.copyOf(plans);
        }

        return List.of((SpellEffectPlan)invoke(GET_EFFECT_PLAN, spell));
    }

    public static void set(final SpellStats0c spell, final List<SpellEffectPlan> plans) {
        if(MULTI_PLAN_API) {
            invoke(SET_EFFECT_PLANS, spell, plans);
            return;
        }

        if(plans.size() != 1) {
            throw new IllegalStateException(
                "Severed Chains supports one spell effect plan, but Irongoon resolved %d; update Severed Chains or select a Dragoon spell effect mode that produces one target plan"
                    .formatted(plans.size())
            );
        }
        invoke(SET_EFFECT_PLAN, spell, plans.getFirst());
    }

    private static Method findMethod(final String name, final Class<?>... parameterTypes) {
        try {
            return SpellStats0c.class.getMethod(name, parameterTypes);
        } catch(final NoSuchMethodException ignored) {
            return null;
        }
    }

    private static Object invoke(final Method method, final SpellStats0c spell, final Object... arguments) {
        try {
            return method.invoke(spell, arguments);
        } catch(final IllegalAccessException exception) {
            throw new IllegalStateException("Cannot access Severed Chains SpellStats0c effect-plan API", exception);
        } catch(final InvocationTargetException exception) {
            if(exception.getCause() instanceof final RuntimeException runtimeException) throw runtimeException;
            if(exception.getCause() instanceof final Error error) throw error;
            throw new IllegalStateException("Severed Chains SpellStats0c effect-plan API failed", exception.getCause());
        }
    }
}
