package com.example.a41400475.juego;

import android.util.Log;
import android.view.MotionEvent;

import org.cocos2d.actions.interval.RotateTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCSize;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 41400475 on 27/9/2016.
 */
public class Juego {
    CCGLSurfaceView _vistaJuego;
    CCSize pantallaDispositivo;
    Sprite pajarito;
    Sprite pinche;
    ArrayList<Sprite> pinches;
    final ThreadLocal<Sprite> imagenFondo = new ThreadLocal<>();

    public Juego(CCGLSurfaceView vistaJuego) {
        _vistaJuego = vistaJuego;
        pinches = new ArrayList<Sprite>();
    }

    public void ComenzarJuego(){
        Log.d("Comenzar", "Comienza el juego");
        Director.sharedDirector().attachInView(_vistaJuego);
        pantallaDispositivo = Director.sharedDirector().displaySize();
        Director.sharedDirector().runWithScene(EscenaJuego());
    }

    private Scene EscenaJuego(){
        Scene escenaDevolver = Scene.node();

        CapaFondo capaFondo = new CapaFondo();
        CapaFrente capaFrente = new CapaFrente();
        escenaDevolver.addChild(capaFondo, -10);
        escenaDevolver.addChild(capaFrente, 10);

        return escenaDevolver;
    }

    class CapaFondo extends Layer {

        public CapaFondo(){
            PonerCapaFondo();
        }

        private void PonerCapaFondo(){
            imagenFondo.set(Sprite.sprite("fondo.png"));
            imagenFondo.get().setPosition(pantallaDispositivo.width/2, pantallaDispositivo.height/2);
            imagenFondo.get().runAction(ScaleBy.action(0.01f, 3.0f, 6.0f));
            super.addChild(imagenFondo.get());
        }

    }

    class CapaFrente extends Layer {

        public CapaFrente(){
            this.setIsTouchEnabled(true);
            PonerPajaritoPosInicial();

            Random random = new Random();
            int cantPinchesIzq = random.nextInt(5 - 3) + 3;;
            int cantPinchesDer = random.nextInt(5 - 3) + 3;

            for (int i =0; i <= cantPinchesIzq; i++){
                PonerPincheIzquierda();
            }

            for (int i =0; i <= cantPinchesDer; i++){
                PonerPincheDerecha();
            }

            TimerTask tareaVerificarImpactos = new TimerTask() {
                @Override
                public void run() {
                    //DetectarColociones();
                }
            };
            Timer relojImpactos = new Timer();
            relojImpactos.schedule(tareaVerificarImpactos, 0, 100);
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event){
            //hacer que cada medio segundo por ej baje 20 la posicion, para que suba y baja y cuando vuelve a tocar sube
            MoverPajarito(0f + pantallaDispositivo.width - pajarito.getWidth()/2, pajarito.getPositionY() + 200f);
            return true;
        }

        @Override
        public boolean ccTouchesMoved(MotionEvent event){
            return true;
        }

        @Override
        public boolean ccTouchesEnded(MotionEvent event){
            //MoverPajarito(event.getX(), pantallaDispositivo.getHeight() - event.getY());
            return true;
        }

        void MoverPajarito(float destinoX, float destinoY){
            pajarito.setPosition(destinoX, destinoY);
            pajarito.runAction(RotateTo.action(0.01f, 270f));
            //pajarito.setPosition(pajarito.getPositionX() - 20f, pajarito.getPositionY() - 40f);

            /*
            float movHorizontal, movVertical, suavizador;
            movHorizontal = destinoX - pantallaDispositivo.getWidth()/2;
            movVertical = destinoY - pantallaDispositivo.getHeight()/2;
            suavizador = 20;
            movHorizontal = movHorizontal/suavizador;
            movVertical = movVertical/suavizador;

            pajarito.setPosition(pajarito.getPositionX()+ movHorizontal, pajarito.getPositionY() + movVertical);
            */
        }

        private void PonerPajaritoPosInicial(){
            pajarito = Sprite.sprite("pajaro.png");

            float posicionInicialX, posicionInicialY;
            posicionInicialX = pantallaDispositivo.width/2;
            posicionInicialY = pantallaDispositivo.height/2;
            pajarito.setPosition(posicionInicialX,posicionInicialY);

            super.addChild(pajarito);
        }

        void PonerPincheIzquierda(){
            pinche = Sprite.sprite("pinche.png");

            CCPoint posInicial = new CCPoint();

            float alturaPinche, anchoPinche;
            alturaPinche = pinche.getHeight();
            anchoPinche = pinche.getWidth();
            posInicial.x = 0f + anchoPinche/2;

            Random generadorAzar = new Random();
            posInicial.y = generadorAzar.nextInt((int) pantallaDispositivo.height - (int) alturaPinche) + alturaPinche/2;

            for (Sprite s : pinches){
                while (posInicial.y == s.getPositionY()){
                    posInicial.y = generadorAzar.nextInt((int) pantallaDispositivo.height - (int) alturaPinche) + alturaPinche/2;
                }
            }

            pinche.setPosition(posInicial.x, posInicial.y);
            pinche.runAction(RotateTo.action(0.01f, 180f));

            pinches.add(pinche);
            super.addChild(pinche);
        }

        void PonerPincheDerecha(){
            pinche = Sprite.sprite("pinche.png");

            CCPoint posInicial = new CCPoint();

            float alturaPinche, anchoPinche;
            alturaPinche = pinche.getHeight();
            anchoPinche = pinche.getWidth();
            //poner en borde inferior derecha
            posInicial.x = 0f + pantallaDispositivo.width - anchoPinche/2;

            Random generadorAzar = new Random();
            posInicial.y = generadorAzar.nextInt((int) pantallaDispositivo.height - (int) alturaPinche) + alturaPinche/2;

            pinche.setPosition(posInicial.x, posInicial.y);

            pinches.add(pinche);
            super.addChild(pinche);
        }

        void DetectarColociones(){
            boolean huboColicion = false;
            for (Sprite pinche : pinches){
                if (InterseccionSprites(pajarito, pinche)){
                    huboColicion = true;
                }
            }
            if (huboColicion){
                Log.d("Colicion", "Hubo colicion");
            } else {
                Log.d("Colicion", "No hubo colicion");
            }
        }

        boolean InterseccionSprites(Sprite sprite1, Sprite sprite2){
            boolean devolver = false;
            int sprite1Izq, sprite1Der, sprite1Abajo, sprite1Arriba;
            int sprite2Izq, sprite2Der, sprite2Abajo, sprite2Arriba;

            sprite1Izq = (int) (sprite1.getPositionX() - sprite1.getWidth()/2);
            sprite1Der = (int) (sprite1.getPositionX() + sprite1.getWidth()/2);
            sprite1Abajo = (int) (sprite1.getPositionY() - sprite1.getHeight()/2);
            sprite1Arriba = (int) (sprite1.getPositionY() + sprite1.getHeight()/2);

            sprite2Izq = (int) (sprite2.getPositionX() - sprite2.getWidth()/2);
            sprite2Der = (int) (sprite2.getPositionX() + sprite2.getWidth()/2);
            sprite2Abajo = (int) (sprite2.getPositionY() - sprite2.getHeight()/2);
            sprite2Arriba = (int) (sprite2.getPositionY() + sprite2.getHeight()/2);

            if (estaEntre(sprite1Izq, sprite2Izq, sprite2Der) &&
                    estaEntre(sprite1Abajo, sprite2Abajo, sprite2Arriba)){
                devolver = true;
            }

            if (estaEntre(sprite1Izq, sprite2Izq, sprite2Der) &&
                    estaEntre(sprite1Arriba, sprite2Abajo, sprite2Arriba)) {
                devolver=true;
            }

            if (estaEntre(sprite1Der, sprite2Izq, sprite2Der) &&
                    estaEntre(sprite1Arriba, sprite2Abajo, sprite2Arriba)) {
                devolver=true;
            }

            if (estaEntre(sprite1Der, sprite2Izq, sprite2Der) &&
                    estaEntre(sprite1Abajo, sprite2Abajo, sprite2Arriba)) {
                devolver=true;
            }

            if (estaEntre(sprite2Izq, sprite1Izq, sprite1Der) &&
                    estaEntre(sprite2Abajo, sprite1Abajo, sprite1Arriba)) {
                devolver=true;
            }

            if (estaEntre(sprite2Izq, sprite1Izq, sprite1Der) &&
                    estaEntre(sprite2Arriba, sprite1Abajo, sprite1Arriba)) {
                devolver=true;
            }

            if (estaEntre(sprite2Der, sprite1Izq, sprite1Der) &&
                    estaEntre(sprite2Arriba, sprite1Abajo, sprite1Arriba)) {
                devolver=true;
            }

            if (estaEntre(sprite2Der, sprite1Izq, sprite1Der) &&
                    estaEntre(sprite2Abajo, sprite1Abajo, sprite1Arriba)) {
                devolver=true;
            }

            return devolver;
        }

        boolean estaEntre (int numComparar, int numMenor, int numMayor){
            boolean devolver;

            if (numMenor > numMayor){
                int auxiliar;
                auxiliar = numMayor;
                numMayor = numMenor;
                numMenor = auxiliar;
            }

            if (numComparar >= numMenor && numComparar <= numMayor){
                devolver = true;
            } else {
                devolver = false;
            }

            return devolver;
        }

    }
}
