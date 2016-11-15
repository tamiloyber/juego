package com.example.a41400475.juego;

import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import org.cocos2d.actions.ease.EaseBounceIn;
import org.cocos2d.actions.instant.CallFunc;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.RotateTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.transitions.SplitRowsTransition;
import org.cocos2d.types.CCColor3B;
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
    Random generadorAzarIzq;
    Random generadorAzarDer;
    int cant = 0;
    Timer relojImpactos;

    public Juego(CCGLSurfaceView vistaJuego) {
        _vistaJuego = vistaJuego;
        pinches = new ArrayList<Sprite>();
        generadorAzarIzq = new Random();
        generadorAzarDer = new Random();
        relojImpactos = new Timer();
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
            imagenFondo.get().runAction(ScaleBy.action(0.01f, 3.0f, 4.0f));
            super.addChild(imagenFondo.get());
        }

    }

    class CapaFrente extends Layer {

        public CapaFrente(){
            this.setIsTouchEnabled(true);

            PonerPajaritoPosInicial();
            PonerPinches();

            TimerTask tareaVerificarImpactos = new TimerTask() {
                @Override
                public void run() {
                    DetectarColociones();
                }
            };
            relojImpactos.schedule(tareaVerificarImpactos, 0, 100);

            TimerTask tareaBajarPajaro = new TimerTask() {
                @Override
                public void run() {
                    MoveTo irAbajo = MoveTo.action(0.8f, pajarito.getPositionX(), pajarito.getPositionY() - 100);
                    IntervalAction secuen = Sequence.actions(irAbajo);
                    pajarito.runAction(secuen);
                }
            };
            Timer reloj = new Timer();
            reloj.schedule(tareaBajarPajaro, 0, 500);
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event){
            cant++;

            MoveTo derecha = MoveTo.action(0.5f, pantallaDispositivo.getWidth(), pajarito.getPositionY() + 200f);
            MoveTo izq = MoveTo.action(0.5f, -pantallaDispositivo.getWidth(), pajarito.getPositionY() + 200f);
            CallFunc finSecuencia = CallFunc.action(this, "FinSecuencia");
            CallFunc cambiarPinches = CallFunc.action(this, "CambiarPinches");

            float posicionInicialX, posicionInicialY;
            posicionInicialX = pantallaDispositivo.width/2;
            posicionInicialY = pantallaDispositivo.height/2;
            IntervalAction secuencia;

            if (cant == 1){
                secuencia = Sequence.actions(derecha, finSecuencia);
                pajarito.runAction(secuencia);

                //si es mayor a cerquita de la pantalla, lo mando para el otro lado
                if (pajarito.getPositionX() > pantallaDispositivo.getWidth() - pajarito.getWidth()) {
                    //girar pajaro para izq --> pajarito = Sprite.sprite("pajaro2.png");
                    MoveTo posInicial = MoveTo.action(0.5f, posicionInicialX, posicionInicialY);
                    secuencia = Sequence.actions(cambiarPinches, posInicial, finSecuencia);
                    pajarito.runAction(secuencia);
                }
            } else {
                //si la posicion es derecha, que vaya para izq y al revez
                /* izquierda:
                    pajarito = Sprite.sprite("pajaro.png");
                 */
            }


            return true;
        }

        @Override
        public boolean ccTouchesMoved(MotionEvent event){
            return true;
        }

        @Override
        public boolean ccTouchesEnded(MotionEvent event){
            return true;
        }

        public void CambiarPinches(){
            //SacarPinches
            for (Sprite pinche : pinches) {
                this.removeChild(pinche, true);
            }
            PonerPinches();
        }

        public void FinSecuencia(){
            Log.d("Fin", "Fin secuencia");

            if (pajarito.getPositionY() == 0f){
                Perdiste();
            }
        }

        void MoverPajarito(float destinoX, float destinoY){
            pajarito.setPosition(destinoX, destinoY);
        }

        private void PonerPajaritoPosInicial(){
            pajarito = Sprite.sprite("pajaro.png");

            float posicionInicialX, posicionInicialY;
            posicionInicialX = pantallaDispositivo.width/2;
            posicionInicialY = pantallaDispositivo.height/2;
            pajarito.setPosition(posicionInicialX,posicionInicialY);

            super.addChild(pajarito);
        }

        public void PonerPincheIzquierda(){
            pinche = Sprite.sprite("pinche.png");

            CCPoint posInicial = new CCPoint();

            float alturaPinche, anchoPinche;
            alturaPinche = pinche.getHeight();
            anchoPinche = pinche.getWidth();
            posInicial.x = 0f + anchoPinche/2;

            Random r = new Random();
            //posInicial.y  = r.nextInt((5 - 1) + 1) * alturaPinche/3;

            posInicial.y = generadorAzarIzq.nextInt((int) pantallaDispositivo.height - (int) alturaPinche) + alturaPinche/2;


            for (Sprite s : pinches){
                boolean resul = InterseccionSprites(s, pinche);
                if (resul) {
                    Log.d("Colicion", "Hubo colicion");
                    posInicial.y = generadorAzarIzq.nextInt((int) pantallaDispositivo.height - (int) alturaPinche) + alturaPinche/2;
                }
            }

            pinche.setPosition(posInicial.x, posInicial.y);
            pinches.add(pinche);
            super.addChild(pinche);
        }

        public void PonerPincheDerecha(){
            pinche = Sprite.sprite("pinche.png");

            CCPoint posInicial = new CCPoint();

            float alturaPinche, anchoPinche;
            alturaPinche = pinche.getHeight();
            anchoPinche = pinche.getWidth();
            //poner en borde inferior derecha
            posInicial.x = 0f + pantallaDispositivo.width - anchoPinche/2;

            posInicial.y = generadorAzarDer.nextInt((int) pantallaDispositivo.height - (int) alturaPinche) + alturaPinche/2;

            for (Sprite s : pinches){
                boolean resul = InterseccionSprites(s, pinche);
                if (resul){
                    Log.d("Colicion", "Hubo colicion");
                    posInicial.y = generadorAzarDer.nextInt((int) pantallaDispositivo.height - (int) alturaPinche) + alturaPinche/2;
                }
            }

            pinche.setPosition(posInicial.x, posInicial.y);
            pinche.runAction(RotateTo.action(0.01f, 180f));
            pinches.add(pinche);
            super.addChild(pinche);
        }

        public void PonerPinches(){
            Random random = new Random();
            int cantPinchesIzq = random.nextInt(5 - 3) + 3;
            int cantPinchesDer = random.nextInt(5 - 3) + 3;

            for (int i =0; i <= cantPinchesIzq; i++){
                PonerPincheIzquierda();
            }

            for (int i =0; i <= cantPinchesDer; i++){
                PonerPincheDerecha();
            }
        }

        public void DetectarColociones(){
            boolean huboColicion = false;
            for (Sprite pinche : pinches){
                if (InterseccionSprites(pajarito, pinche)){
                    huboColicion = true;
                }
            }
            if (huboColicion){
                Log.d("Colicion", "Hubo colicion");
                relojImpactos.cancel();
                Perdiste();
            }
        }

        public boolean InterseccionSprites(Sprite sprite1, Sprite sprite2){
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

        public boolean estaEntre (int numComparar, int numMenor, int numMayor){
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


        public void Perdiste(){
            Perdiste perdiste = new Perdiste(_vistaJuego);
            perdiste.Comenzar();
        }

    }
}
