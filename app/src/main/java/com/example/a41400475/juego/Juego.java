package com.example.a41400475.juego;

import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import org.cocos2d.actions.base.Action;
import org.cocos2d.actions.ease.EaseBounceIn;
import org.cocos2d.actions.instant.CallFunc;
import org.cocos2d.actions.interval.DelayTime;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.JumpBy;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.RotateTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.ColorLayer;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.transitions.SplitRowsTransition;
import org.cocos2d.types.CCColor3B;
import org.cocos2d.types.CCColor4B;
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
    Action abajoAction;

    public Juego(CCGLSurfaceView vistaJuego) {
        _vistaJuego = vistaJuego;
        pinches = new ArrayList<Sprite>();
    }

    public void PantallaPrincipal(){
        Director.sharedDirector().attachInView(_vistaJuego);
        pantallaDispositivo = Director.sharedDirector().displaySize();
        Director.sharedDirector().runWithScene(EscenaPrincipal());
    }

    private Scene EscenaPrincipal(){
        Scene escenaPrincipal = Scene.node();

        CapaFondo capaFondo = new CapaFondo();
        CapaPrincipalFrente cpFrente = new CapaPrincipalFrente();

        escenaPrincipal.addChild(capaFondo, -10);
        escenaPrincipal.addChild(cpFrente, 10);
        return escenaPrincipal;
    }

    class CapaPrincipalFrente extends Layer {
        public CapaPrincipalFrente() { PonerCapaPrincipalFrente(); }

        private void PonerCapaPrincipalFrente() {
            Label bienvenidos = Label.label("¡BIENVENIDOS!", "Calibri", 80);
            CCColor3B color = new CCColor3B(0,169,79);
            bienvenidos.setColor(color);
            bienvenidos.setString("Bienvenidos");
            bienvenidos.setPosition(pantallaDispositivo.width/2, pantallaDispositivo.getHeight()/2);

            MenuItemImage boton = MenuItemImage.item("play.png", "play.png", this, "ComenzarJuego");
            float posX, posY;
            posX = boton.getWidth()/2;
            posY = boton.getHeight()/2;
            boton.setPosition(posX, posY);

            Menu botones = Menu.menu(boton);
            botones.setPosition(pantallaDispositivo.width/2 - boton.getWidth()/2, pantallaDispositivo.getHeight()/2 - (boton.getHeight() + 90));

            super.addChild(bienvenidos);
            super.addChild(botones);
        }

        public void ComenzarJuego(){
            Log.d("Comenzar", "Comienza el juego");
            Director.sharedDirector().runWithScene(EscenaJuego());
        }
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

        TimerTask tareaBajarPajaro;
        Random generadorAzarIzq;
        Random generadorAzarDer;
        Timer relojImpactos;
        TimerTask tareaVerificarImpactos;

        public CapaFrente(){
            this.setIsTouchEnabled(true);
            generadorAzarIzq = new Random();
            generadorAzarDer = new Random();
            relojImpactos = new Timer();

            pajarito = Sprite.sprite("pajaro.png");
            PonerPajaritoPosInicial();
            PonerPinches();

            tareaVerificarImpactos = new TimerTask() {
                @Override
                public void run() {
                    DetectarColociones();
                }
            };
            relojImpactos.schedule(tareaVerificarImpactos, 0, 100);

            tareaBajarPajaro = new TimerTask() {
                @Override
                public void run() {
                    if (pajarito.getPositionY() > 0f + pajarito.getHeight()) {
                        MoveTo irAbajo = MoveTo.action(0.6f, pajarito.getPositionX(), pajarito.getPositionY() - 100);
                        IntervalAction secuen = Sequence.actions(irAbajo);
                        abajoAction=pajarito.runAction(secuen);
                    }

                    float posicionInicialX, posicionInicialY;
                    posicionInicialX = pantallaDispositivo.width/2;
                    posicionInicialY = pantallaDispositivo.height/2;

                    IntervalAction secuencia;
                    //si es mayor a cerquita de la pantalla, lo mando para el otro lado
                    if (pajarito.getPositionX() >= pantallaDispositivo.getWidth() - pajarito.getWidth()/2) {
                        //girar pajaro para izq --> pajarito = Sprite.sprite("pajaro2.png");
                        MoveTo posInicial = MoveTo.action(0.4f, posicionInicialX, posicionInicialY);
                        MoveTo izq = MoveTo.action(0.4f, - pantallaDispositivo.getWidth(), pajarito.getPositionY() + 100f);
                        CallFunc cambiarPinches = CallFunc.action(this, "CambiarPinches");
                        //CallFunc parar = CallFunc.action(this, "Parar");
                        secuencia = Sequence.actions( posInicial, cambiarPinches, izq);
                        pajarito.runAction(secuencia);
                        //Iniciar();
                    }
                }
            };
            Timer reloj = new Timer();
            reloj.schedule(tareaBajarPajaro, 500, 500);
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event){
            pajarito.stopAction(abajoAction);
            MoveTo derecha = MoveTo.action(0.4f, pantallaDispositivo.getWidth() - pajarito.getWidth()/2, pajarito.getPositionY() + 100f);
            pajarito.runAction(derecha);

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

        public void Parar(){
            tareaVerificarImpactos.cancel();
        }

        public void Iniciar(){
            tareaVerificarImpactos.run();
        }

        void MoverPajarito(float destinoX, float destinoY){
            pajarito.setPosition(destinoX, destinoY);
        }

        public void PonerPajaritoPosInicial(){
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

            //Random r = new Random();
            //posInicial.y  = r.nextInt((5 - 1) + 1) * alturaPinche/3;

            posInicial.y = generadorAzarIzq.nextInt((int) pantallaDispositivo.height - (int) alturaPinche) + alturaPinche/2;


            for (Sprite s : pinches){
                boolean resul = InterseccionSprites(s, pinche);
                if (resul) {
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
            int cantPinchesIzq = random.nextInt(4 - 2) + 2;
            int cantPinchesDer = random.nextInt(4 - 2) + 2;

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
                    break;
                }
            }
            if (huboColicion){
                Log.d("Colicion", "Hubo colicion");
                Perder();
                tareaVerificarImpactos.cancel();
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
            if (numMenor > numMayor){
                int auxiliar;
                auxiliar = numMayor;
                numMayor = numMenor;
                numMenor = auxiliar;
            }

            if (numComparar >= numMenor && numComparar <= numMayor){
                return true;
            } else {
                return false;
            }
        }


        public void Perder(){
            removeAllChildren(true);
            pinches = new ArrayList<>();
            Director.sharedDirector().replaceScene(EscenaPerdiste());
        }

    }

    private Scene EscenaPerdiste(){
        Scene escenaDevolver = Scene.node();
        Perdiste perdiste = new Perdiste();
        escenaDevolver.addChild(perdiste, 10);

        return escenaDevolver;
    }

    private class Perdiste extends Layer {
        Label perdiste;

        public Perdiste() {
            this.setIsTouchEnabled(true);
            perdiste = Label.label("Perdiste", "Verdana", 50);
            perdiste.setString("Perdiste!");
            CCColor3B color = new CCColor3B(255, 255, 255);
            perdiste.setColor(color);
            perdiste.setPosition(pantallaDispositivo.width / 2, pantallaDispositivo.height / 2);
            addChild(perdiste);
            this.runAction(Sequence.actions(DelayTime.action(2.0f), CallFunc.action(this, "PerdisteDone")));
        }

        public void PerdisteDone() {
            Director.sharedDirector().replaceScene(EscenaPrincipal());
        }
    }
}
