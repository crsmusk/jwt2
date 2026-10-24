package com.seguridad.practica.servicio.details;

import com.seguridad.practica.repositorio.RolesRepo;
import org.springframework.stereotype.Service;

import com.seguridad.practica.configuracion.seguridad.SecurityConfig;
import com.seguridad.practica.entidades.Role;
import com.seguridad.practica.entidades.Usuario;
import com.seguridad.practica.entidades.roles;
import com.seguridad.practica.entidades.dtos.AuthResponse;
import com.seguridad.practica.entidades.dtos.iniciarSesion;
import com.seguridad.practica.entidades.dtos.registrarUsuario;
import com.seguridad.practica.jwt.utiles;
import com.seguridad.practica.repositorio.UsuarioRepo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;



@Service 
public class UserDetailService implements UserDetailsService {

    
    private final PasswordConfig passwordConfig;
    private final UsuarioRepo repo;
    private final utiles jwtUtiles;
    private final RolesRepo rolesRepo;
  

    public UserDetailService( UsuarioRepo repo, utiles jwtUtiles, RolesRepo rolesRepo, PasswordConfig passwordConfig) {
        this.repo = repo;
        this.jwtUtiles = jwtUtiles;
        this.rolesRepo = rolesRepo;
        this.passwordConfig = passwordConfig;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Usuario usuario = repo.findByUsername(username)
               .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        List <GrantedAuthority> authorities = new ArrayList<>();
        for (Role rol : usuario.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_".concat(rol.getRol().toString())));
        }
        
        return new User(usuario.getUsername(), usuario.getPassword(), authorities);
    }
    
    public AuthResponse loginUser(iniciarSesion authLoginRequest){
        String username=authLoginRequest.username();
        String password=authLoginRequest.password();
        
        Authentication authentication=this.authentication(username,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
       
        String accessToken = jwtUtiles.generarToken(authentication);
        
        AuthResponse authResponse=new AuthResponse(username,"user logged succes",accessToken,true);
        return authResponse;

    }


    private Authentication authentication(String username, String password) {
    UserDetails userDetails;
    try {
        userDetails = this.loadUserByUsername(username);
    } catch (UsernameNotFoundException e) {
        throw new BadCredentialsException("Usuario o contraseña incorrectos");
    }

    if (!passwordConfig.passwordEncoder().matches(password, userDetails.getPassword())) {
        throw new BadCredentialsException("Usuario o contraseña incorrectos");
    }

    return new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
}
    
    
 public AuthResponse createUser(registrarUsuario authCreateUserRequest){
        String username=authCreateUserRequest.username();
        String password=authCreateUserRequest.password();
        String email=authCreateUserRequest.email();
        if (repo.existsByUsername(username)) {
          throw new RuntimeException("El nombre de usuario ya existe");
         }
        if (repo.existsByEmail(email)) {
        throw new RuntimeException("El email ya está registrado");
        }
        Usuario user=new Usuario();
        user.setUsername(username);
        user.setPassword(passwordConfig.passwordEncoder().encode(password));
        user.setEmail(email);

        Set<Role> roles = new HashSet<>();
        roles.add(buscarRol());
        
        user.setRoles(roles);

        Usuario userCreated=repo.save(user);
        List <GrantedAuthority> authorities = new ArrayList<>();
        userCreated.getRoles().forEach(role->authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRol().toString()))));
        Authentication authentication=new UsernamePasswordAuthenticationToken(userCreated.getUsername(),userCreated.getPassword(),authorities);

        String accessToken=jwtUtiles.generarToken(authentication);
        AuthResponse authResponse=new AuthResponse(userCreated.getUsername(),"user created successfully",accessToken,true);
        return authResponse;

    }
    
    private Role buscarRol() {
    return rolesRepo.findByRol(roles.USER)
        .orElseGet(() -> {
            Role r = new Role();
            r.setRol(roles.USER);
            return rolesRepo.save(r);
        });
}


    
}
