package io.bootique;

import com.google.inject.ProvisionException;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.config.ConfigurationFactory;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.ConfigPathOptionMetadata;
import io.bootique.run.Runner;
import io.bootique.unit.BQInternalTestFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class Bootique_CliConfigOptionsIT {

    @Rule
    public BQInternalTestFactory runtimeFactory = new BQInternalTestFactory();

    @Test
    public void testOption_OverrideConfig() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-1=x")
                .module(binder -> BQCoreModule
                        .extend(binder)
                        .addOption("c.m.l", "opt-1"))
                .createRuntime();

        Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");

        Assert.assertEquals("x", bean1.c.m.l);
    }

    @Test
    public void testOption_DefValue_DefValueOverrideConfig() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-1")
                .module(binder -> BQCoreModule
                        .extend(binder)
                        .addOption("c.m.l", "1", "opt-1"))
                .createRuntime();

        Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");

        Assert.assertEquals("1", bean1.c.m.l);
    }

    @Test
    public void testOption_PathAbsentInYAML_OverrideConfigObject() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-1=x")
                .module(binder -> BQCoreModule
                        .extend(binder)
                        .addOption("c.m.f", "opt-1"))
                .createRuntime();
        Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");

        Assert.assertEquals("x", bean1.c.m.f);
    }

    @Test
    public void testOption_PathAbsentInYAML_DefValueOverrideConfigObject() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-1")
                .module(binder -> BQCoreModule
                        .extend(binder)
                        .addOption("c.m.f", "y", "opt-1"))
                .createRuntime();
        Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");

        Assert.assertEquals("y", bean1.c.m.f);
    }

    @Test
    public void testOption_PathAbsentInYAML_ValueOnCLIApplied() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-1=x")
                .module(binder -> BQCoreModule
                        .extend(binder)
                        .addOption("c.m.f", "y", "opt-1"))
                .createRuntime();
        Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");

        Assert.assertEquals("x", bean1.c.m.f);
    }

    @Test
    public void testOptions_PathOverlapped_OverrideConfigInCLIOrder() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-2=2", "--opt-3=3")
                .module(binder -> BQCoreModule.extend(binder).addOption("c.m.k", "opt-1")
                        .addOption("c.m.k", "opt-2")
                        .addOption("c.m.k", "opt-3"))
                .createRuntime();
        Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");

        Assert.assertEquals(3, bean1.c.m.k);
    }

    @Test
    public void testOptions_PathOverlappedAndDefValues_OverrideConfigInCLIOrderWithDefValues() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-2", "--opt-3")
                .module(binder -> BQCoreModule.extend(binder).addOption("c.m.k", "opt-1")
                        .addOption("c.m.k", "2", "opt-2")
                        .addOption("c.m.k", "3", "opt-3"))
                .createRuntime();
        Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");

        Assert.assertEquals(3, bean1.c.m.k);
    }

    @Test
    public void testOptions_CommandAndModuleContributeTheOption_ModuleOptionOverrideConfig() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--cmd-1", "--opt-1=2")
                .module(binder -> BQCoreModule.extend(binder)
                        .addOption("c.m.k", "opt-1")
                        .addCommand(new TestOptionCommand1()))
                .createRuntime();

        Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");
        Runner runner = runtime.getInstance(Runner.class);

        runner.run();

        Assert.assertEquals(2, bean1.c.m.k);
    }

    @Test
    public void testConfigOptionsOrder_PropsVarsOptionsFileOptions() {
        System.setProperty("bq.c.m.f", "prop_c_m_f");

        try {
            BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--file-opt-1", "--opt-1=Option")
                    .module(binder -> BQCoreModule.extend(binder)
                            .addOption("c.m.f", "opt-1")
                            .addConfigResourceOption("classpath:io/bootique/config/configTest4Opt1.yml", "file-opt-1")
                            .setVar("BQ_C_M_F", "var_c_m_f"))
                    .createRuntime();

            Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");
            Assert.assertEquals("f", bean1.c.m.f);
        } finally {
            System.clearProperty("bq.c.m.f");
        }
    }

    @Test(expected = ProvisionException.class)
    public void testOptions_NotMappedConfigPath_ExceptionThrown() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--opt-1=x")
                .module(binder -> BQCoreModule.extend(binder).addOption("c.m.k.x", "opt-1"))
                .createRuntime();

        runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");
    }

    @Test
    public void testOptionConfigFile_OverrideConfig() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--file-opt")
                .module(binder -> BQCoreModule.extend(binder)
                        .addConfigResourceOption("classpath:io/bootique/config/configTest4.yml", "file-opt"))
                .createRuntime();
        Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");

        Assert.assertEquals("x", bean1.c.m.l);
    }

    @Test
    public void testMultipleOptionsConfigFiles_OverrideInCLIOrder() {
        BQRuntime runtime = runtimeFactory.app("--config=classpath:io/bootique/config/test4.yml", "--file-opt-2", "--opt-1=x", "--file-opt-1")
                .module(binder -> BQCoreModule.extend(binder)
                        .addConfigResourceOption("classpath:io/bootique/config/configTest4Opt1.yml", "file-opt-1")
                        .addConfigResourceOption("classpath:io/bootique/config/configTest4Opt2.yml", "file-opt-2")
                        .addOption("c.m.f", "opt-1"))
                .createRuntime();
        Bean1 bean1 = runtime.getInstance(ConfigurationFactory.class).config(Bean1.class, "");

        Assert.assertEquals(3, bean1.c.m.k);
        Assert.assertEquals("f", bean1.c.m.f);
    }

    static final class TestOptionCommand1 extends CommandWithMetadata {

        public TestOptionCommand1() {
            super(CommandMetadata.builder(TestOptionCommand1.class)
                    .name("cmd-1")
                    .addOption(ConfigPathOptionMetadata.builder("opt-1")
                            .configPath("c.m.f").defaultValue("3")));
        }

        @Override
        public CommandOutcome run(Cli cli) {
            return CommandOutcome.succeeded();
        }
    }

    static class Bean1 {
        private String a;
        private Bean2 c;

        public void setA(String a) {
            this.a = a;
        }

        public void setC(Bean2 c) {
            this.c = c;
        }
    }

    static class Bean2 {

        private Bean3 m;

        public void setM(Bean3 m) {
            this.m = m;
        }
    }

    static class Bean3 {
        private int k;
        private String f;
        private String l;

        public void setK(int k) {
            this.k = k;
        }

        public void setF(String f) {
            this.f = f;
        }

        public void setL(String l) {
            this.l = l;
        }
    }
}
