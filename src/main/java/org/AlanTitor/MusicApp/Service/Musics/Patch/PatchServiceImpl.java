package org.AlanTitor.MusicApp.Service.Musics.Patch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.springframework.stereotype.Service;

import java.nio.file.FileAlreadyExistsException;

@AllArgsConstructor
@Service
public class PatchServiceImpl implements PatchService{

    private final ObjectMapper objectMapper;

    @Override
    public Music patchMusic(Music music, JsonPatch patch) throws JsonPatchException, JsonProcessingException, FileAlreadyExistsException {

        JsonNode originNode = objectMapper.convertValue(music, JsonNode.class);
        JsonNode patchedNode = patch.apply(originNode);

        Music patchedMusic = objectMapper.treeToValue(patchedNode, Music.class);

        if(patchedMusic.getName().equals(music.getName())){
            throw new FileAlreadyExistsException("File already exists!");
        }

        patchedMusic.setId(music.getId());
        patchedMusic.setName(patchedMusic.getName() + music.getFileExtension());
        patchedMusic.setAuthorId(music.getAuthorId());

        return patchedMusic;
    }
}