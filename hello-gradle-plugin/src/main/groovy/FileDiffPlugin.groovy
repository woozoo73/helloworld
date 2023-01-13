import org.gradle.api.Plugin
import org.gradle.api.Project

class FileDiffPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('fileDiff', FileDiffExtension)

        project.task.create('fileDiff', FileDiffTask) {
            file1 = project.fileDiff.file1
        }
    }

}
