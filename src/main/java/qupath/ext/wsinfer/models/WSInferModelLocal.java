/**
 * Copyright 2023 University of Edinburgh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qupath.ext.wsinfer.models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class WSInferModelLocal extends WSInferModel {

    private final Path modelDirectory;
    private static final ResourceBundle resources = ResourceBundle.getBundle("qupath.ext.wsinfer.ui.strings");

    /**
     * Try to create a WSInfer model from a user directory.
     * @param modelDirectory A user directory containing a
     *                       torchscript.pt file and a config.json file
     * @return A {@link WSInferModel} if the directory supplied is valid,
     * otherwise nothing.
     */
    public static WSInferModelLocal createInstance(Path modelDirectory) throws IOException {
        return new WSInferModelLocal(modelDirectory);
    }

    private WSInferModelLocal(Path modelDirectory) throws IOException {
        this.modelDirectory = modelDirectory;
        this.hfRepoId = modelDirectory.getFileName().toString();
        try (var stream = Files.list(modelDirectory)) {
            var files = stream.toList();
            if (!files.contains(getConfigFile().toPath())) {
                throw new IOException(resources.getString("error.localModel") + ": " + getConfigFile().toString());
            }
            if (!files.contains(getTorchScriptFile().toPath())) {
                throw new IOException(resources.getString("error.localModel") + ": " + getTorchScriptFile().toString());
            }
        }
    }

    @Override
    File getModelDirectory() {
        return this.modelDirectory.toFile();
    }

    @Override
    public boolean isValid() {
        return getTorchScriptFile().exists() && getConfiguration() != null;
    }

    @Override
    public synchronized void downloadModel() {}

    @Override
    public synchronized void removeCache() {}
}
