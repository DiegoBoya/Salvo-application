package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.logout.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    //tengo definido abajo de todo esta clase, con el autowired las conecto
    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
        {

        }
    }


    @Bean
    public CommandLineRunner initData(GameRepository gameRepository,
                                      PlayerRepository playerRepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shipRepository,
                                      SalvoRepository salvoRepository,
                                      ScoreRepository scoreRepository) {
        return (args) -> {
            // Players
            Player player1 = playerRepository.save(new Player("j.bauer@ctu.gov", passwordEncoder().encode("24")));
            Player player2 = playerRepository.save(new Player("c.obrian@ctu.gov", passwordEncoder().encode("42")));
            Player player3 = playerRepository.save(new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb")));
            Player player4 = playerRepository.save(new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole")));
            // Player player5 = playerRepository.save(new Player("pepe_argento@ctu.gov", "pass"));


            Game game1 = gameRepository.save(new Game(LocalDateTime.now()));
            //el plusHours agrega una hora a la hora actual
            Game game2 = gameRepository.save(new Game(LocalDateTime.now().plusHours(1)));
            Game game3 = gameRepository.save(new Game(LocalDateTime.now().plusHours(2)));
            Game game4 = gameRepository.save(new Game(LocalDateTime.now().plusHours(3)));
            Game game5 = gameRepository.save(new Game(LocalDateTime.now().plusHours(4)));
            Game game6 = gameRepository.save(new Game(LocalDateTime.now().plusHours(5)));
            Game game7 = gameRepository.save(new Game(LocalDateTime.now().plusHours(6)));
            Game game8 = gameRepository.save(new Game(LocalDateTime.now().plusHours(7)));


            // GampePlayer, esto que sucede aca, es crear filas en la tabla GamePlayer!!!!

            //partida 1
            GamePlayer gp1 = gamePlayerRepository.save(new GamePlayer(player1, game1)); //j.bauer
            GamePlayer gp2 = gamePlayerRepository.save(new GamePlayer(player2, game1));//c.obrian
            // partida 2
            GamePlayer gp3 = gamePlayerRepository.save(new GamePlayer(player1, game2));//j.bauer
            GamePlayer gp4 = gamePlayerRepository.save(new GamePlayer(player2, game2));//c.obrian
            //partida 3
            GamePlayer gp5 = gamePlayerRepository.save(new GamePlayer(player2, game3));//c.obrian
            GamePlayer gp6 = gamePlayerRepository.save(new GamePlayer(player4, game3));//t.almeida
            //partida 4
            GamePlayer gp7 = gamePlayerRepository.save(new GamePlayer(player2, game4));//c.obrian
            GamePlayer gp8 = gamePlayerRepository.save(new GamePlayer(player1, game4));//j.bauer
            //partida 5
            GamePlayer gp9 = gamePlayerRepository.save(new GamePlayer(player4, game5));//t.almeida
            GamePlayer gp10 = gamePlayerRepository.save(new GamePlayer(player1, game5));//j.bauer
            //partida 6
            GamePlayer gp11 = gamePlayerRepository.save(new GamePlayer(player3, game6));//kim_bauer
            // partida 7
            GamePlayer gp12 = gamePlayerRepository.save(new GamePlayer(player4, game7));//t.almeida
            //partida 8
            GamePlayer gp13 = gamePlayerRepository.save(new GamePlayer(player3, game8));//kim_bauer
            GamePlayer gp14 = gamePlayerRepository.save(new GamePlayer(player4, game8));//t.almeida

            // -------------SHIPS-------------------------------
            List<String> ubication;
            List<String> shots;

            //GAME1
            //player1 game1 --- j.bauer
            Ship ship1 = shipRepository.save(new Ship(ShipTypeEnum.DESTROYER, gp1, ubication = Arrays.asList("H2", "H3", "H4")));
            Ship ship2 = shipRepository.save(new Ship(ShipTypeEnum.SUBMARINE, gp1, ubication = Arrays.asList("E1", "F1", "G1")));
            Ship ship3 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp1, ubication = Arrays.asList("B4", "B5")));

            Salvo salvo1 = salvoRepository.save(new Salvo(gp1, 1, Arrays.asList("B5", "C5", "F1")));
            Salvo salvo2 = salvoRepository.save(new Salvo(gp1, 2, Arrays.asList("F2", "D5")));


            //player 2 game1--- c.obrian
            Ship ship4 = shipRepository.save(new Ship(ShipTypeEnum.DESTROYER, gp2, ubication = Arrays.asList("B5", "C5", "D5")));
            Ship ship5 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp2, ubication = Arrays.asList("F1", "F2")));

            Salvo salvo3 = salvoRepository.save(new Salvo(gp2, 1, Arrays.asList("B3", "B4", "B5")));
            Salvo salvo4 = salvoRepository.save(new Salvo(gp2, 2, Arrays.asList("E1", "H3", "A2")));

            //GAME2
            //player 1 game2 --- j.bauer
            Ship ship6 = shipRepository.save(new Ship(ShipTypeEnum.DESTROYER, gp3, ubication = Arrays.asList("B5", "C5", "D5")));
            Ship ship7 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp3, ubication = Arrays.asList("C6", "C7")));

            Salvo salvo5 = salvoRepository.save(new Salvo(gp3, 1, Arrays.asList("A2", "A4", "G6")));
            Salvo salvo6 = salvoRepository.save(new Salvo(gp3, 2, Arrays.asList("A3", "H6")));


            //player 2 game2--- c.obrian
            Ship ship8 = shipRepository.save(new Ship(ShipTypeEnum.SUBMARINE, gp4, ubication = Arrays.asList("A2", "A3", "A4")));
            Ship ship9 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp4, ubication = Arrays.asList("G6", "H6")));

            Salvo salvo7 = salvoRepository.save(new Salvo(gp4, 1, Arrays.asList("B5", "D5", "C7")));
            Salvo salvo8 = salvoRepository.save(new Salvo(gp4, 2, Arrays.asList("C5", "C6")));


            //GAME3
            //player 2 game3 --- c.obrian
            Ship ship10 = shipRepository.save(new Ship(ShipTypeEnum.DESTROYER, gp5, ubication = Arrays.asList("B5", "C5", "D5")));
            Ship ship11 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp5, ubication = Arrays.asList("C6", "C7")));

            Salvo salvo9 = salvoRepository.save(new Salvo(gp5, 1, Arrays.asList("G6", "H6", "A4")));
            Salvo salvo10 = salvoRepository.save(new Salvo(gp5, 2, Arrays.asList("A2", "A3", "D8")));

            //player 4 game3--- t.almeida
            Ship ship12 = shipRepository.save(new Ship(ShipTypeEnum.SUBMARINE, gp6, ubication = Arrays.asList("A2", "A3", "A4")));
            Ship ship13 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp6, ubication = Arrays.asList("G6", "H6")));

            Salvo salvo11 = salvoRepository.save(new Salvo(gp6, 1, Arrays.asList("H1", "H2", "H3")));
            Salvo salvo12 = salvoRepository.save(new Salvo(gp6, 2, Arrays.asList("E1", "F2", "G3")));

            //GAME4
            //player 2 game4 --- c.obrian
            Ship ship14 = shipRepository.save(new Ship(ShipTypeEnum.DESTROYER, gp7, ubication = Arrays.asList("B5", "C5", "D5")));
            Ship ship15 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp7, ubication = Arrays.asList("C6", "C7")));

            Salvo salvo13 = salvoRepository.save(new Salvo(gp7, 1, Arrays.asList("A3", "A4", "A7")));
            Salvo salvo14 = salvoRepository.save(new Salvo(gp7, 2, Arrays.asList("A2", "G6", "H6")));

            //player 2 game4--- j.bauer
            Ship ship16 = shipRepository.save(new Ship(ShipTypeEnum.SUBMARINE, gp8, ubication = Arrays.asList("A2", "A3", "A4")));
            Ship ship17 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp8, ubication = Arrays.asList("G6", "H6")));

            Salvo salvo18 = salvoRepository.save(new Salvo(gp8, 1, Arrays.asList("B5", "C6", "H1")));
            Salvo salvo19 = salvoRepository.save(new Salvo(gp8, 2, Arrays.asList("C5", "C7", "D5")));

            //GAME5
            //player 4 game5 --- t.almeida
            Ship ship18 = shipRepository.save(new Ship(ShipTypeEnum.DESTROYER, gp9, ubication = Arrays.asList("B5", "C5", "D5")));
            Ship ship19 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp9, ubication = Arrays.asList("C6", "C7")));

            Salvo salvo20 = salvoRepository.save(new Salvo(gp9, 1, Arrays.asList("A1", "A2", "A3")));
            Salvo salvo21 = salvoRepository.save(new Salvo(gp9, 2, Arrays.asList("G6", "G7", "G8")));

            //player 2 game5 --- j.bauer
            Ship ship20 = shipRepository.save(new Ship(ShipTypeEnum.SUBMARINE, gp10, ubication = Arrays.asList("A2", "A3", "A4")));
            Ship ship21 = shipRepository.save(new Ship(ShipTypeEnum.BARQUITO_FIESTERO, gp10, ubication = Arrays.asList("G6", "H6")));

            Salvo salvo22 = salvoRepository.save(new Salvo(gp10, 1, Arrays.asList("B5", "B6", "C7")));
            Salvo salvo23 = salvoRepository.save(new Salvo(gp10, 2, Arrays.asList("C6", "D6", "E6")));
            Salvo salvo24 = salvoRepository.save(new Salvo(gp10, 3, Arrays.asList("H1", "H8")));

            //GAME6 - el solitario
            //player 3 game6 --- kim_bauer
            Ship ship22 = shipRepository.save(new Ship(ShipTypeEnum.DESTROYER, gp9, ubication = Arrays.asList("B5", "C5", "D5")));
            Ship ship23 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp9, ubication = Arrays.asList("C6", "C7")));

            //GAME8
            //player 3 game8 --- t.kim_bauer
            Ship ship24 = shipRepository.save(new Ship(ShipTypeEnum.DESTROYER, gp9, ubication = Arrays.asList("B5", "C5", "D5")));
            Ship ship25 = shipRepository.save(new Ship(ShipTypeEnum.PATROL_BOAT, gp9, ubication = Arrays.asList("C6", "C7")));

            //player 2 game8 --- j.bauer
            Ship ship26 = shipRepository.save(new Ship(ShipTypeEnum.SUBMARINE, gp10, ubication = Arrays.asList("A2", "A3", "A4")));
            Ship ship27 = shipRepository.save(new Ship(ShipTypeEnum.BARQUITO_FIESTERO, gp10, ubication = Arrays.asList("G6", "H6")));


            //Score score1 = scoreRepository.save(new Score(player1, game1, 1));
            //al cargar los scores los player que le paso a cada game deben coincidir en como cargue los gamePlayer, sino
            // traera vacio los scores, porque no va encontrar al id del player , relacionado al getScore de player
            //score1 tiene lo que tiene cargado el gp1
            //score2 tiene lo que tiene cargado el gp2
            //score6 tiene lo de gp6

            Score score1 = scoreRepository.save(new Score(player1, game1, 1, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"))));
            Score score2 = scoreRepository.save(new Score(player2, game1, 0, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"))));

            //Game 2
            Score score3 = scoreRepository.save(new Score(player1, game2, 0.5f, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(1)));
            Score score4 = scoreRepository.save(new Score(player2, game2, 0.5f, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(1)));

            //Game 3
            Score score5 = scoreRepository.save(new Score(player2, game3, 1, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(2)));
            Score score6 = scoreRepository.save(new Score(player4, game3, 0, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(2)));

            //Game 4
            Score score7 = scoreRepository.save(new Score(player2, game4, 0.5f, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(1)));
            Score score8 = scoreRepository.save(new Score(player1, game4, 0.5f, LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(1)));

            //Game 5 esta partida esta en curso pero no termino, lo mismo para game6, 7 y 8
            //Score score9 =scoreRepository.save(new Score(player4,game5,1,LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(2)));
            //Score score10 =scoreRepository.save(new Score(player1,game5,1,LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).plusHours(2)));


        };


    }

    //esto encripta las contraseñas, se las pasamos en el player usando este metodo para que las encripte
    // ver constructor de player arriba en esta clase
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}


//--------------termina la clase SalvoAplication ---------------------


// ------------------------- aca se hace la autorizacion -----------------------------------
//-------------------------- recibe USS y PASS -------------------------------------------------------
//esta clase global tiene acceso al player repository, y puede manejar la info que tiene,
// la clase WebSecurityConfiguration  usa la BD que tenemos para autenticar al uss
    @Configuration
    class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName -> {
            Player player = playerRepository.findByUserName(inputName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}

// ------------------------- aca se hace la autorizacion -----------------------------------
//-------------------------- ASIGNACION DE ROLES -------------------------------------------------------
        @Configuration
        @EnableWebSecurity
        class WebSecurityConfig extends WebSecurityConfigurerAdapter {

            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.authorizeRequests()
                        .antMatchers("/web/**").permitAll()
                        .antMatchers("/h2-console/").permitAll()
                        .antMatchers("/api/players").permitAll()
                        .antMatchers("/api/games").permitAll()
                        .antMatchers("/api/game_view/**").hasAuthority("USER")
                        //.antMatchers("/web/game.html").hasAuthority("USER")
                        .and().csrf().ignoringAntMatchers("/h2-console/**")
                        .and().headers().frameOptions().sameOrigin();


                http.formLogin()
                        .usernameParameter("name")
                        .passwordParameter("pwd")
                        .loginPage("/api/login");

                http.logout().logoutUrl("/api/logout");

                // turn off checking for CSRF tokens
                http.csrf().disable();

                // if user is not authenticated, just send an authentication failure response
                http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

                // if login is successful, just clear the flags asking for authentication
                http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

                // if login fails, just send an authentication failure response
                http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

                // if logout is successful, just send a success response
                http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
            }

            private void clearAuthenticationAttributes(HttpServletRequest request) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
                }
            }
        }


