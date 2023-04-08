package service;

import com.hrblizz.fileapi.controller.exception.BadRequestException;
import com.hrblizz.fileapi.controller.exception.InternalException;
import com.hrblizz.fileapi.data.entities.FileEntity;
import com.hrblizz.fileapi.data.repository.FileEntityRepository;
import com.hrblizz.fileapi.dto.*;
import com.hrblizz.fileapi.service.FileService;
import com.hrblizz.fileapi.service.impl.FileServiceImpl;
import com.mongodb.MongoException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {

    @Mock
    private FileEntityRepository fileEntityRepository;

    private Path uploadPath;

    private FileService fileService;

    private File directory;

    @Before
    public void setUp() {
        String directoryName = "test";
        directory = new File(directoryName);
        directory.mkdir();
        saveFile(directoryName, "test-download.txt");
        saveFile(directoryName, "test-delete.txt");
        saveFile(directoryName, "file-exists.txt");
        uploadPath = Paths.get("test");
        fileService = new FileServiceImpl(fileEntityRepository, uploadPath);
    }

    @Test
    public void testSave() {
        // given
        byte[] content = "test file content".getBytes();
        String filename = "testSave.txt";
        String contentType = "text/plain";
        String token = "token";
        String source = "test source";

        MockMultipartFile multipartFile = new MockMultipartFile("test", filename, "text/plain", content);

        FileUploadRequestDTO dto = new FileUploadRequestDTO();
        dto.setName(filename);
        dto.setContent(multipartFile);
        dto.setMeta("{\"creatorEmployeeId\": 1}");
        dto.setSource(source);
        dto.setContentType(contentType);
        dto.setContent(multipartFile);

        FileEntity entity = FileEntity.builder()
                .token(token)
                .fileName(filename)
                .size(1024L)
                .contentType(contentType)
                .build();

        when(fileEntityRepository.save(any())).thenReturn(entity);

        // when
        FileUploadResponseDTO response = fileService.save(dto);

        // then
        assertTrue(StringUtils.isNotBlank(response.getToken()));
    }

    @Test
    public void testSaveWithBadRequest() {
        // given
        byte[] content = "test file content".getBytes();
        String filename = "file-exists.txt";
        String contentType = "text/plain";
        String source = "test source";

        MockMultipartFile multipartFile = new MockMultipartFile("file-exists", filename, "text/plain", content);

        FileUploadRequestDTO dto = new FileUploadRequestDTO();
        dto.setName(filename);
        dto.setContent(multipartFile);
        dto.setMeta("{\"creatorEmployeeId\": 1}");
        dto.setSource(source);
        dto.setContentType(contentType);
        dto.setContent(multipartFile);


        // when and then
        assertThrows(BadRequestException.class, () -> fileService.save(dto));
    }

    @Test
    public void testSaveWithInternalException() {
        // given
        byte[] content = "test file content".getBytes();
        String filename = "testSave.txt";
        String contentType = "text/plain";
        String source = "test source";

        MockMultipartFile multipartFile = new MockMultipartFile("test", filename, "text/plain", content);

        FileUploadRequestDTO dto = new FileUploadRequestDTO();
        dto.setName(filename);
        dto.setContent(multipartFile);
        dto.setMeta("{\"creatorEmployeeId\": 1}");
        dto.setSource(source);
        dto.setContentType(contentType);
        dto.setContent(multipartFile);

        doThrow(new MongoException("Mongo exception")).when(fileEntityRepository).save(any());

        // when and then
        assertThrows(InternalException.class, () -> fileService.save(dto));
    }

    @Test
    public void testGetMetas() {
        // given
        String token = "token1";
        FileEntity entity = FileEntity.builder().token(token)
                .fileName("test.txt")
                .contentType("text/plain")
                .size(100L)
                .build();

        List<String> tokens = Collections.singletonList(token);
        when(fileEntityRepository.findAllById(tokens)).thenReturn(Collections.singletonList(entity));

        FileMetaRequestDTO requestDTO = new FileMetaRequestDTO();
        requestDTO.setTokens(tokens);

        // when
        FileMetaResponseDTO response = fileService.getMetas(requestDTO);

        // then
        assertNotNull(response.getFiles());
        assertEquals(1, response.getFiles().size());
        Map<String, FileMetaDTO> metas = response.getFiles();
        assertNotNull(metas.get(token));
        assertEquals(entity.getContentType(), metas.get(token).getContentType());
        assertEquals(entity.getSize(), metas.get(token).getSize());
    }

    @Test
    public void testGetMetasWithInternalException() {
        // given
        String token = "token1";

        List<String> tokens = Collections.singletonList(token);
        FileMetaRequestDTO dto = new FileMetaRequestDTO();
        dto.setTokens(tokens);

        doThrow(new MongoException("Mongo exception")).when(fileEntityRepository).findAllById(any());

        // when and then
        assertThrows(InternalException.class, () -> fileService.getMetas(dto));
    }

    @Test
    public void testDownloadFile() {
        // given
        String token = "token";
        String filename = "test-download.txt";
        String contentType = "text/plain";
        Long size = 100L;
        LocalDateTime createTime = LocalDateTime.now();

        FileEntity entity = FileEntity.builder()
                .token(token)
                .fileName(filename)
                .contentType(contentType)
                .size(size)
                .createTime(createTime)
                .build();

        when(fileEntityRepository.findById(token)).thenReturn(Optional.of(entity));

        // when
        FileData response = fileService.downloadFile(token);

        // then
        assertNotNull(response.getContent());
        assertEquals(entity.getContentType(), response.getContentType());
        assertEquals(entity.getSize(), response.getSize());
        assertEquals(entity.getCreateTime(), response.getCreateTime());
        assertEquals(entity.getFileName(), response.getFileName());
    }

    @Test
    public void testDownloadFileWithInternalException() {
        // given
        String token = "token";
        String filename = "non-existent-file.txt";

        FileEntity entity = FileEntity.builder()
                .token(token)
                .fileName(filename)
                .build();

        when(fileEntityRepository.findById(token)).thenReturn(Optional.of(entity));

        assertThrows(InternalException.class, () -> fileService.downloadFile(token));
    }

    @Test
    public void testDownloadFileWithBadRequest() {
        // given
        String token = "non-existent-token";

        when(fileEntityRepository.findById(token)).thenReturn(Optional.empty());

        // when and then
        assertThrows(BadRequestException.class, () -> fileService.downloadFile(token));
    }

    @Test
    public void testDeleteFile() {
        // given
        String token = "token";
        String fileName = "test-delete.txt";
        File file = new File(directory + "/" + fileName);

        FileServiceImpl fileService = new FileServiceImpl(fileEntityRepository, uploadPath);

        FileEntity fileEntity = FileEntity.builder()
                .fileName(fileName)
                .token(token)
                .build();

        when(fileEntityRepository.findById(token)).thenReturn(Optional.of(fileEntity));
        doNothing().when(fileEntityRepository).deleteById(token);

        // when
        fileService.deleteFile(token);

        // then
        verify(fileEntityRepository, times(1)).deleteById(token);
        assertFalse(file.exists());
    }

    @Test
    public void testDeleteFileWithNonexistentToken() {
        // given
        String token = "nonexistent-file-token";

        FileServiceImpl fileService = new FileServiceImpl(fileEntityRepository, uploadPath);

        when(fileEntityRepository.findById(token)).thenReturn(Optional.empty());

        // when and then
        assertThrows(BadRequestException.class, () -> fileService.deleteFile(token));
    }

    @Test
    public void testDeleteFileWithInternalException() {
        // given
        String token = "token";
        String fileName = "non-existing-file.txt";

        FileServiceImpl fileService = new FileServiceImpl(fileEntityRepository, uploadPath);

        FileEntity fileEntity = FileEntity.builder()
                .fileName(fileName)
                .token(token)
                .build();

        when(fileEntityRepository.findById(token)).thenReturn(Optional.of(fileEntity));

        // when and then
        assertThrows(InternalException.class, () -> fileService.deleteFile(token));
    }

    private void saveFile(String directoryName, String fileName) {
        try {
            File file = new File(directoryName + "/" + fileName);
            byte[] content = "test file content".getBytes();
            OutputStream outputStream = Files.newOutputStream(file.toPath());
            outputStream.write(content);
        } catch (IOException ex) {
            // Do nothing;
        }
    }

    @After
    public void clearResources() {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }

        directory.delete();
    }
}
