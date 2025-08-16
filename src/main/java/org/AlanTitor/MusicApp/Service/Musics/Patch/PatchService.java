package org.AlanTitor.MusicApp.Service.Musics.Patch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.AlanTitor.MusicApp.Entity.Musics.Music;

import java.nio.file.FileAlreadyExistsException;

public interface PatchService {
    Music patchMusic(Music music, JsonPatch patch) throws JsonPatchException, JsonProcessingException, FileAlreadyExistsException;
}
