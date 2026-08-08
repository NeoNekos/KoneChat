package cc.yamrc.konechat.hypertext;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

public final class HypertextCoreTestMain {
    private static int passed;

    private HypertextCoreTestMain() {
    }

    public static void main(String[] args) {
        run("named colors and scopes", HypertextCoreTestMain::namedColors);
        run("hex and gradient colors", HypertextCoreTestMain::gradientColors);
        run("literal and regex component rules", HypertextCoreTestMain::componentRules);
        run("conditions and null handlers", HypertextCoreTestMain::conditionsAndNull);
        run("style boundaries and escapes", HypertextCoreTestMain::boundaries);
        run("nested gradients keep outer cursor", HypertextCoreTestMain::nestedGradients);
        run("handlers receive complete literal source", HypertextCoreTestMain::completeSource);
        System.out.println("Hypertext core tests: " + passed + " passed, 0 failed");
    }

    private static void namedColors() {
        Component result = render("A<red>B</red><bold>C</bold>");
        equal("ABC", result.getString());
        Component red = result.getSiblings().get(1);
        check(red.getStyle().getColor() != null && red.getStyle().getColor().getValue() == 0xFF5555,
                "red style missing");
        check(result.getSiblings().get(2).getStyle().isBold(), "bold style missing");
    }

    private static void gradientColors() {
        Component result = render("<gradient:#ff0000:#00ff00>AB</gradient>");
        equal("AB", result.getString());
        equal(0xFF0000, result.getSiblings().get(0).getStyle().getColor().getValue());
        equal(0x00FF00, result.getSiblings().get(1).getStyle().getColor().getValue());
    }

    private static void componentRules() {
        ComponentRule literal = new ComponentRule("example:item", 0,
                ComponentRuleStrategies.literal("[i]"), context -> true,
                context -> Component.literal("ITEM"));
        ComponentRule regex = new ComponentRule("example:word", -1,
                ComponentRuleStrategies.regex("@([A-Za-z]+)"), context -> true,
                context -> Component.literal(context.match().getGroups().get("1").toUpperCase()));
        Component result = render("A[i] @Bob", List.of(literal, regex));
        equal("AITEM BOB", result.getString());
    }

    private static void conditionsAndNull() {
        ComponentRule rejected = new ComponentRule("example:rejected", 10,
                ComponentRuleStrategies.literal("[x]"), context -> false,
                context -> Component.literal("wrong"));
        ComponentRule nullRule = new ComponentRule("example:null", 0,
                ComponentRuleStrategies.literal("[x]"), context -> true,
                context -> null);
        equal("[x]", render("[x]", List.of(rejected, nullRule)).getString());
    }

    private static void boundaries() {
        ComponentRule rule = new ComponentRule("example:item", 0,
                ComponentRuleStrategies.literal("[i]"), context -> true,
                context -> Component.literal("ITEM"));
        equal("[i]", render("<red>[i</red>]", List.of(rule)).getString());
        equal("[i]", render("\\[i]", List.of(rule)).getString());
    }

    private static void nestedGradients() {
        Component result = render("<gradient:#ff0000:#0000ff>A<gradient:#00ff00:#ffff00>BC</gradient>D</gradient>");
        equal("ABCD", result.getString());
        equal(0xFF0000, result.getSiblings().get(0).getStyle().getColor().getValue());
        equal(0x00FF00, result.getSiblings().get(1).getStyle().getColor().getValue());
        equal(0xFFFF00, result.getSiblings().get(2).getStyle().getColor().getValue());
        equal(0x0000FF, result.getSiblings().get(3).getStyle().getColor().getValue());
    }

    private static void completeSource() {
        String[] seen = {null};
        ComponentRule rule = new ComponentRule("example:item", 0,
                ComponentRuleStrategies.literal("[x]"), context -> true,
                context -> {
                    seen[0] = context.source();
                    return Component.literal("X");
                });
        equal("AXB", render("A[x]B", List.of(rule)).getString());
        equal("A[x]B", seen[0]);
    }

    private static Component render(String text) {
        return render(text, List.of());
    }

    private static Component render(String text, List<ComponentRule> rules) {
        return HypertextEngine.render(text, HypertextConfig.defaults(), rules, new Object());
    }

    private static void run(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("PASS " + name);
        } catch (Throwable throwable) {
            System.err.println("FAIL " + name + ": " + throwable.getMessage());
            throw throwable;
        }
    }

    private static void equal(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
}
