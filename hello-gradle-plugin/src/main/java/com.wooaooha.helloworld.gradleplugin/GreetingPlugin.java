package com.wooaooha.helloworld.gradleplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GreetingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("greeting", GreetingPluginExtension.class);
        project.task("hello", task -> {

        });
    }

}
