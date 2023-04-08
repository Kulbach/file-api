package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrblizz.fileapi.controller.FileController;
import com.hrblizz.fileapi.controller.exception.InternalException;
import com.hrblizz.fileapi.dto.*;
import com.hrblizz.fileapi.security.ApiAuthenticationEntryPoint;
import com.hrblizz.fileapi.security.ApiAuthenticationProvider;
import com.hrblizz.fileapi.security.WebSecurityConfig;
import com.hrblizz.fileapi.service.FileService;
import configuration.FileConfigurationMock;
import org.junit.Test;

import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FileController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = FileConfigurationMock.class)
@Import({WebSecurityConfig.class, FileController.class})
@WithMockUser("spring")
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

    @MockBean
    private ApiAuthenticationProvider apiAuthenticationProvider;

    @Test
    @DisplayName("test file upload success")
    public void uploadFileSuccess() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile("content", "test.pdf", "application/pdf", "test data".getBytes());

        // Create a mock FileUploadResponseDTO
        FileUploadResponseDTO responseDTO = new FileUploadResponseDTO("test_token");

        // Set up the mock service to return the mock response
        when(fileService.save(any())).thenReturn(responseDTO);

        // Perform the request and check the response
        mockMvc.perform(multipart("/files")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("name", "test")
                        .param("contentType", "application/pdf")
                        .param("meta", "\"meta\":\"meta\"")
                        .param("source", "source"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test_token"));
    }

    @Test
    @DisplayName("test file upload bad request when file name is missing")
    public void uploadFileBadRequest() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile("content", "test.pdf", "application/pdf", "test data".getBytes());

        // Create a mock FileUploadResponseDTO
        FileUploadResponseDTO responseDTO = new FileUploadResponseDTO("test_token");

        // Set up the mock service to return the mock response
        when(fileService.save(any())).thenReturn(responseDTO);

        // Perform the request and check the response
        mockMvc.perform(multipart("/files")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("contentType", "application/pdf")
                        .param("meta", "\"meta\":\"meta\"")
                        .param("source", "source"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("test file upload returns service unavailable")
    public void uploadFileServiceUnavailable() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile("content", "test.pdf", "application/pdf", "test data".getBytes());

        // Set up the mock service to return the mock response
        when(fileService.save(any())).thenThrow(InternalException.class);

        // Perform the request and check the response
        mockMvc.perform(multipart("/files")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("name", "test")
                        .param("contentType", "application/pdf")
                        .param("meta", "test")
                        .param("source", "source"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("test get files metas")
    public void testGetMetas() throws Exception {
        // Given
        FileMetaRequestDTO requestDto = new FileMetaRequestDTO();
        requestDto.setTokens(Arrays.asList("token1", "token2"));

        Map<String, FileMetaDTO> files = new HashMap<>();
        files.put("token1", FileMetaDTO.builder().token("token1")
                .fileName("file1.txt")
                .size(100L)
                .contentType("text/plain")
                .createTime(LocalDateTime.now())
                .source("source1")
                .meta(new HashMap<>())
                .build());
        files.put("token2", FileMetaDTO.builder().token("token1")
                .fileName("file2.txt")
                .size(200L)
                .contentType("text/plain")
                .createTime(LocalDateTime.now())
                .source("source2")
                .meta(new HashMap<>())
                .build());
        FileMetaResponseDTO responseDto = new FileMetaResponseDTO(files);

        // Set up the mock service to return the mock response
        when(fileService.getMetas(requestDto)).thenReturn(responseDto);

        // Perform the request and check the response
        mockMvc.perform(post("/files/metas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.files.token1.fileName").value("file1.txt"))
                .andExpect(jsonPath("$.files.token1.size").value(100))
                .andExpect(jsonPath("$.files.token2.fileName").value("file2.txt"))
                .andExpect(jsonPath("$.files.token2.size").value(200));
    }

    @Test
    @DisplayName("test get files metas returns service unavailable")
    public void testGetMetasServiceUnavailable() throws Exception {
        // Given
        FileMetaRequestDTO requestDto = new FileMetaRequestDTO();
        requestDto.setTokens(Arrays.asList("token1", "token2"));

        // Set up the mock service to return the mock response
        when(fileService.getMetas(requestDto)).thenThrow(InternalException.class);

        // Perform the request and check the response
        mockMvc.perform(post("/files/metas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("test download file")
    public void testDownloadFile() throws Exception {
        String token = "testToken";
        LocalDateTime createTime = LocalDateTime.now();
        String fileName = "testFileName";
        String contentType = "application/pdf";
        Long size = 1024L;
        byte[] content = "test".getBytes();

        FileData fileData = FileData.builder()
                .fileName(fileName)
                .contentType(contentType)
                .createTime(createTime)
                .size(size)
                .content(new InputStreamResource(new ByteArrayInputStream(content)))
                .build();

        // Set up the mock service to return the mock response
        when(fileService.downloadFile(token)).thenReturn(fileData);

        MockHttpServletResponse response = mockMvc.perform(get("/file/" + token))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Filename", fileName))
                .andExpect(header().string("X-Filesize", String.valueOf(size)))
                .andExpect(header().string("X-CreateTime", createTime.toString()))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, contentType))
                .andReturn().getResponse();

        assertArrayEquals(response.getContentAsByteArray(), content);
    }

    @Test
    @DisplayName("test download file returns service unavailable")
    public void testDownloadFileServiceUnavailable() throws Exception {
        // Set up the mock service to return the mock response
        when(fileService.downloadFile(any())).thenThrow(InternalException.class);

        mockMvc.perform(get("/file/testToken"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("test delete file")
    public void testDeleteFile() throws Exception {
        // Given
        String token = "my_token";
        // Do nothing when delete file
        doNothing().when(fileService).deleteFile(token);

        // When
        mockMvc.perform(delete("/file/" + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test delete file")
    public void testDeleteFileServiceUnavailable() throws Exception {
        // Given
        String token = "my_token";
        // Throw exception when delete file
        willThrow(InternalException.class).given(fileService).deleteFile(token);

        // When
        mockMvc.perform(delete("/file/" + token))
                .andExpect(status().isServiceUnavailable());
    }
}
