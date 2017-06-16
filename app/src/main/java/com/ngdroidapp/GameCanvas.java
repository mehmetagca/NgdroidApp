package com.ngdroidapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;
import java.util.Vector;

import istanbul.gamelab.ngdroid.base.BaseCanvas;
import istanbul.gamelab.ngdroid.core.NgMediaPlayer;
import istanbul.gamelab.ngdroid.util.Log;
import istanbul.gamelab.ngdroid.util.Utils;


/**
 * Created by noyan on 24.06.2016.
 * Nitra Games Ltd.
 */


public class GameCanvas extends BaseCanvas {

    private Bitmap tileset, spritesheet, bullet, enemy, explode;
    private Rect tilesrc, tiledst, spritesrc, spritedst, bulletsrc, enemysrc, enemydst, explodesrc, explodedst;

    private int kareno, animasyonno, animasyonyonu, bulletoffsetx_temp, bulletoffsety_temp;

    private int hiz, hizx, hizy, spritex, spritey, bulletspeed, explodeframeno;
    private int bulletx_temp, bullety_temp;
    private int sesefekti_patlama;

    private boolean enemyexist, exploded, donmeboolean;

    private NgMediaPlayer arkaplan_muzik;

    private int enemyspeedx, enemyspeedy, enemyx, enemyy, donmenoktasi;

    private Random enemyrnd;

    int touchx, touchy;//Ekranda bastigimiz yerlerin koordinatlari

    public Vector<Rect> bulletdst;
    public Vector<Integer> bulletx2, bullety2, bulletoffsetx2, bulletoffsety2, bulletspeedx2, bulletspeedy2;

    public GameCanvas(NgApp ngApp) {
        super(ngApp);
    }

    public void setup() {
        //Log.i(TAG, "setup");
        tileset = Utils.loadImage(root,"images/tilea2.png");
        tilesrc = new Rect();
        tiledst = new Rect();

        spritesheet = Utils.loadImage(root,"images/cowboy.png");

        spritesrc = new Rect();
        spritedst = new Rect();

        bullet = Utils.loadImage(root,"images/bullet.png");
        bulletsrc = new Rect();

        kareno=0;

        animasyonno = 1;

        animasyonyonu = 0;

        hiz = 16;
        hizx = 0;
        hizy = 0;
        spritex = 0;
        spritey = 0;

        bulletspeed = 0;

        bulletoffsetx_temp = 256;
        bulletoffsety_temp = 128;

        bulletx_temp = 0;
        bullety_temp = 0;

        bulletdst = new Vector<>();
        bulletx2 = new Vector<>();
        bullety2 = new Vector<>();
        bulletspeedx2 = new Vector<>();
        bulletspeedy2 = new Vector<>();
        bulletoffsetx2 = new Vector<>();
        bulletoffsety2 = new Vector<>();

        enemyexist = true;
        enemy = Utils.loadImage(root,"images/mainship03.png");
        enemysrc = new Rect();
        enemydst = new Rect();

        enemyspeedx = 10;
        enemyspeedy = 0;
        enemyx = getWidthHalf()-128;
        enemyy = getHeight()-256;

        explode = Utils.loadImage(root,"images/exp2_0.png");
        explodesrc = new Rect();
        explodedst = new Rect();
        explodeframeno = 0;

        exploded = false;//mermi patladimi?

        try {
            sesefekti_patlama = root.soundManager.load("sounds/se1.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //region arkaplanmuzik // Acilir kapanir menu haline getirmek icin yazdik.
        arkaplan_muzik = new NgMediaPlayer(root);
        arkaplan_muzik.load("sounds/m2.mp3");
        arkaplan_muzik.setVolume(0.5f);
        arkaplan_muzik.prepare();
        arkaplan_muzik.start();
        //endregion

        donmenoktasi = getWidth();
        donmeboolean = true;

        enemyrnd = new Random();
    }



    public void update() {
        //Log.i(TAG, "mehmet agca");

        tilesrc.set(0,0,64,64);

        if(donmeboolean)
        {
            if (enemyspeedx > 0)
            {
                donmenoktasi = enemyrnd.nextInt(getWidth()-256-(enemyx+50)) + enemyx;
            }
            else if(enemyspeedx < 0)
            {
                donmenoktasi = enemyrnd.nextInt(enemyx);
            }
            donmeboolean = false;
        }

        if(enemyspeedx > 0 && enemyx > donmenoktasi)
        {
            donmeboolean = true;
            enemyspeedx = -enemyspeedx;
        }
        else if(enemyspeedx < 0 && enemyx < donmenoktasi)
        {
            donmeboolean = true;
            enemyspeedx = -enemyspeedx;
        }
        spritex += hizx;
        spritey += hizy;

        /*for(int i=0; i < bulletx2.size(); i++)
        {
            bulletx2.set(i, bulletx2.elementAt(i) + bulletspeedx2.elementAt(i));//icindeki elemani degistirmeye calisiyoruz
            bullety2.set(i, bullety2.elementAt(i) + bulletspeedy2.elementAt(i));
        }*/

        if(spritex+256 > getWidth() || spritex < 0) {//x ekseni icin sona geldimi kontrolu
            hizx = 0;//spritex = getWidth() - 256;
        }

        if(spritey+256 > getHeight() || spritey < 0){//y ekseni icin sona geldimi kontrolu
            hizy = 0;//spritey = getHeight() -256;
        }

        if(animasyonno == 1)
            kareno++;
        else if(animasyonno == 0)
            kareno = 0;

        if(kareno > 8)
            kareno=1;

        if(hizx > 0)
            animasyonyonu = 0;
        else if(hizy > 0)
            animasyonyonu = 9;

        if(Math.abs(hizx) > 0 || Math.abs(hizy) > 0)
            animasyonno = 1;
        else
            animasyonno = 0;

        spritesrc.set(kareno*128, animasyonyonu*128,(kareno+1)*128, (animasyonyonu+1)*128);//Resimden aldigimiz koordinatlar
        spritedst.set(spritex, spritey, spritex+256, spritey+256);//Ekrana cizilecegi koordinatlar

        bulletsrc.set(0,0,70,70);
        //bulletdst.set(bulletx_temp, bullety_temp, bulletx_temp + 32, bullety_temp + 32);

        for(int i=0; i < bulletx2.size(); i++)
        {
            bulletdst.elementAt(i).set(bulletx2.elementAt(i), bullety2.elementAt(i), bulletx2.elementAt(i) + 32, bullety2.elementAt(i) + 32);
        }

        if(enemyexist)
        {
            enemysrc.set(0, 0, 64, 64);
            //enemydst.set(getWidthHalf() - 128, getHeight() - 256, getWidthHalf() + 128, getHeight());
            enemydst.set(enemyx, enemyy, enemyx+256, enemyy+256);
        }

        for(int i = 0; i < bulletdst.size(); i++)
        {
            if(enemydst.contains(bulletdst.elementAt(i))) // enemy ve bullet kesistimi kontrolu yapiliyor.
            {
                explodedst.set(bulletx2.elementAt(i)-64, bullety2.elementAt(i)-64, bulletx2.elementAt(i)+64, bullety2.elementAt(i)+64);

                bulletdst.removeElementAt(i);
                bulletx2.removeElementAt(i);
                bullety2.removeElementAt(i);
                bulletspeedx2.removeElementAt(i);
                bulletspeedy2.removeElementAt(i);

                enemyexist = false;
                enemydst.set(0,0,0,0);
                exploded = true;

                root.soundManager.play(sesefekti_patlama);
            }
        }

        explodesrc = getexplodeframe(explodeframeno);

        if(exploded)
            explodeframeno+=3;

        if(explodeframeno > 15)
        {
            explodeframeno = 0;
            exploded = false;
        }

        enemyx += enemyspeedx;
        enemyy += enemyspeedy;

        if(enemyx + 256 > getWidth() || enemyx < 0)
            enemyspeedx = -enemyspeedx; // sınırlara gelince geri donmesi icin eksi ile carptik.
    }

    public void draw(Canvas canvas) {
        //Log.i(TAG, "draw");

        for (int i=0; i<getWidth(); i+=128)
        {
            for(int j=0; j<getHeight(); j+=128)
            {
                tiledst.set(i,j,i+128,j+128);
                canvas.drawBitmap(tileset,tilesrc,tiledst,null);//yesil cimen zemini  tum ekrana cizme
            }
        }

        for(int i=0; i < bulletx2.size(); i++)
        {
            bulletx2.set(i, bulletx2.elementAt(i) + bulletspeedx2.elementAt(i));//icindeki elemani degistirmeye calisiyoruz
            bullety2.set(i, bullety2.elementAt(i) + bulletspeedy2.elementAt(i));

            if(bulletx2.elementAt(i) > getWidth() || bulletx2.elementAt(i) < 0 || bullety2.elementAt(i) > getHeight() || bullety2.elementAt(i) < 0)//Mermiler ekran disina ciktiysa silebiliriz.
            {
                bulletx2.removeElementAt(i);
                bullety2.removeElementAt(i);
                //bulletoffsetx2.removeElementAt(i);
                //bulletoffsety2.removeElementAt(i);
                bulletdst.removeElementAt(i);
                bulletspeedx2.removeElementAt(i);
                bulletspeedy2.removeElementAt(i);
            }
            //Log.i("Control: ", String.valueOf(bulletx2.size()));
        }

        canvas.drawBitmap(spritesheet,spritesrc,spritedst,null);

        //canvas.drawBitmap(bullet,bulletsrc,bulletdst,null);

        for(int i = 0; i < bulletdst.size(); i++)
            canvas.drawBitmap(bullet,bulletsrc, bulletdst.elementAt(i),null);

        if(enemyexist)
            canvas.drawBitmap(enemy, enemysrc, enemydst, null);

        if(exploded)
            canvas.drawBitmap(explode, explodesrc, explodedst, null);
    }

    public Rect getexplodeframe(int frameno)
    {
        frameno = 15-frameno;
        Rect temp = new Rect();
        temp.set((frameno%4)*64, (frameno/4)*64, ((frameno%4) + 1)*64, ((frameno/4) + 1)*64);//4 e bolmenin nedeni frameno 4 iken 4/4=1 yani 2. satira inmesini sagladik.
        return temp;
    }

    public void keyPressed(int key) {

    }

    public void keyReleased(int key) {

    }

    public boolean backPressed() {
        return true;
    }

    public void surfaceChanged(int width, int height) {

    }

    public void surfaceCreated() {

    }

    public void surfaceDestroyed() {

    }

    public void touchDown(int x, int y) {
        touchx = x;
        touchy = y;
    }

    public void touchMove(int x, int y) {
    }

    public void touchUp(int x, int y) {
        if((x - touchx) > 100)//saga cektiysek
        {
            animasyonno = 1;
            animasyonyonu = 0;

            hizx = hiz;
            hizy = 0;
        }
        else if((touchx - x) > 100)//sola cektiysek
        {
            animasyonno = 1;
            animasyonyonu = 1;

            hizx = -hiz;
            hizy = 0;
        }
        else if((y - touchy) > 100)//asagi cektiysek
        {
            animasyonno = 1;
            animasyonyonu = 9;

            hizy = hiz;
            hizx = 0;
        }
        else if((touchy - y) > 100)//yukari cektiysek
        {
            animasyonno = 1;
            animasyonyonu = 5;

            hizy = -hiz;
            hizx = 0;
        }
        else//mouse ile 100px den az bir degisim yaptiysak
        {
            animasyonno = 0;

            hizx = 0;
            hizy = 0;

            bulletspeed = 32;

            if(animasyonyonu == 0)
            {
                bulletspeedx2.add(bulletspeed);
                bulletspeedy2.add(0);
                //bulletspeedx = bulletspeed;
                //bulletspeedy = 0;

                bulletoffsetx_temp = 256;
                bulletoffsety_temp = 128;
            }
            else if(animasyonyonu == 1)
            {
                bulletspeedx2.add(-bulletspeed);
                bulletspeedy2.add(0);
                //bulletspeedx = -bulletspeed;
                //bulletspeedy = 0;

                bulletoffsetx_temp = 0;
                bulletoffsety_temp = 128;
            }
            else if(animasyonyonu == 9)
            {
                bulletspeedy2.add(bulletspeed);
                bulletspeedx2.add(0);
                //bulletspeedy = bulletspeed;
                //bulletspeedx = 0;

                bulletoffsetx_temp = 128;
                bulletoffsety_temp = 256;
            }
            else if(animasyonyonu == 5)
            {
                bulletspeedy2.add(-bulletspeed);
                bulletspeedx2.add(0);
                //bulletspeedy = -bulletspeed;
                //bulletspeedx = 0;

                bulletoffsetx_temp = 128;
                bulletoffsety_temp = 0;
            }

            bulletx2.add(spritex + bulletoffsetx_temp);
            bullety2.add(spritey + bulletoffsety_temp);

            bulletx_temp = spritex + bulletoffsetx_temp;
            bullety_temp = spritey + bulletoffsety_temp;

            bulletdst.add(new Rect(bulletx_temp, bullety_temp, bulletx_temp + 32, bullety_temp + 32));
        }
    }


    public void pause() {

    }


    public void resume() {

    }


    public void reloadTextures() {

    }


    public void showNotify() {
    }

    public void hideNotify() {
    }

}
