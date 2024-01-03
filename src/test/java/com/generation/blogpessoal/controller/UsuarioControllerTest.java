package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();

		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", ""));
	}

	@Test
	@DisplayName("😀Cadastrar Usuário")
	public void deveCriarUmUsuario() {

		// Corpo da Requisição
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Gabriel Rodrigues", "gabriel@email.com.br", "12345678", ""));

		// Resposta da Requisição HTTP
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		// Verifica o HTTP Status Code
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("😀Login do Usuário")
	public void deveAutenticarUmUsuario() {

		// Cadastra um usuário
		Optional<Usuario> usuarioCadastrado = usuarioService
				.cadastrarUsuario(new Usuario(0L, "Gabriel Rodrigues", "gabrielr@email.com", "root1234", ""));

		// Cria um usuarioLogin que recebe os dados de login do usuarioCadastrado
		UsuarioLogin usuarioLogin = new UsuarioLogin();
		usuarioLogin.setUsuario(usuarioCadastrado.get().getUsuario());
		usuarioLogin.setSenha("root1234");

		// Corpo da Requisição
		HttpEntity<UsuarioLogin> corpoRequisicao = new HttpEntity<UsuarioLogin>(usuarioLogin);

		// Resposta da Requisição HTTP
		ResponseEntity<UsuarioLogin> corpoResposta = testRestTemplate.exchange("/usuarios/logar", HttpMethod.POST,
				corpoRequisicao, UsuarioLogin.class);

		// Verifica o HTTP Status Code
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("😀Não Deve Duplicar Usuário")
	public void naoDeveDuplicarUsuario() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Amanda Tsai", "amanda@email.com.br", "12345678", ""));

		// Corpo da Requisição
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Amanda Tsai", "amanda@email.com.br", "12345678", ""));

		// Resposta da Requisição HTTP
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		// Verifica o HTTP Status Code
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("😀Deve Atualizar Usuário")
	public void deveAtualizarUmUsuario() {

		Optional<Usuario> usuarioCadastrado = usuarioService
				.cadastrarUsuario(new Usuario(0L, "Kendal Katherine", "kendal@email.com.br", "12345678", ""));

		// Corpo da Requisição
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(usuarioCadastrado.get().getId(),
				"Kendal Katherine Correia", "kendalk@email.com.br", "78945612", ""));

		// Resposta da Requisição HTTP
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

		// Verifica o HTTP Status Code
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("😀Deve Listar Todos Os Usuários")
	public void deveListarTodosUsuarios() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Vitor Nascimento", "vitor@email.com.br", "12345678", ""));

		usuarioService.cadastrarUsuario(new Usuario(0L, "Samara Almeida", "samara@email.com.br", "12345678", ""));

		// Resposta da Requisição HTTP
		ResponseEntity<String> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

		// Verifica o HTTP Status Code
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("😀Deve Listar Um Usuário Específico")
	public void deveListarUmUsuario() {

		// Cadastra um usuário
		Optional<Usuario> usuarioCadastrado = usuarioService
				.cadastrarUsuario(new Usuario(0L, "Gabriel Sponda", "gabriels@email.com", "12345678", ""));

		// Resposta da Requisição HTTP
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/" + usuarioCadastrado.get().getId(), HttpMethod.GET, null, Usuario.class);

		// Verifica o HTTP Status Code
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}

}
