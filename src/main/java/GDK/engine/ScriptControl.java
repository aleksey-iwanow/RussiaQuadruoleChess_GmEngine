package GDK.engine;
import GDK.engine.components.Transform;

import java.util.ArrayList;

public abstract class ScriptControl {
    public GameObject gameObject;
    public ArrayList<GameObject> collisionGameObjects = new ArrayList<>();
    public Transform transform;
    public boolean isHover;

    public void init(GameObject gameObject, Transform transform){
        this.gameObject = gameObject;
        this.transform = transform;
    }
    public @interface Coroutine {

    }

    public void debug(String line){
        System.out.println(line);
    }
    public void debug(Vector2 vec){
        System.out.println("x: " + vec.x + " | y: " + vec.y);
    }
    public void debug(double line){
        System.out.println(line);
    }
    public void debug(int line){
        System.out.println(line);
    }
    public void invoke(Runnable method, int msTime){
        startCoroutine(method, msTime, false, true);
    }

    /**
     * выполняется при обновлении кадра
     */
    public void update() {

    }
    /**
     * выполняется при обновлении кадра ПОСЛЕ update()
     */
    public void lateUpdate(){

    }
    /**
     * выполняется при наведении мышки на объект
     */
    public void onHover(){

    }
    /**
     * выполняется при наведении мышки на объект только первый раз
     */
    public void onHoverOnce(){

    }
    /**
     * выполняется при выходе из наведения мышки на объект
     */
    public void onHoverExit(){

    }
    /**
     * выполняется после инициализации GameObject
     */
    public void start(){

    }

    /**
     * выполняется после инициализации GameObject и перед start
     */
    public void awake() {

    }

    /**
     @return GameObject по имени
     */
    public GameObject find(String name) { return Engine.find(name); }
    public <T> ArrayList<T> findAllWithScript(Class<T> t) { return Engine.findAllWithScript(t); }
    public static <T> T findWithScript(Class<T> type_) { return Engine.findWithScript(type_);}
    public ArrayList<GameObject> findAll(String name) { return Engine.findAll(name); }

    public <T> String nameof(Class<T> clazz){
        return clazz.getName();
    }
    /**
     * удаление игрового объекта
     */
    public void destroy(){
        destroy(gameObject);
    }

    public void destroy(GameObject deletion){
        deletion.destroyChild();
        Engine.destroy(deletion);
    }
    public void destroy(ScriptControl script){
        if (script == null) return;
        destroy(script.gameObject); }

    /**
     @return Сomponent объекта по типу T
     */
    public <T> T getComponent(Class<T> type){
        return gameObject.getComponent(type);
    }

    public void startCoroutine(Runnable method, int timeMs, boolean playOnStart){
        startCoroutine(method, timeMs, playOnStart, false);
    }

    public void startCoroutine(Runnable method, int timeMs, boolean playOnStart, boolean isOneShot){
        CoroutineObject coroutine = new CoroutineObject(method, timeMs, playOnStart, isOneShot);
        Main.startCoroutine(coroutine);
    }
    public GameObject instantiate(String path) {
        return getNewGameObject(path);
    }
    public GameObject instantiate(String path, ScriptControl parent) {
        return instantiate(path, parent.gameObject);
    }
    public GameObject instantiate(String path, GameObject parent) {
        GameObject inst = getNewGameObject(path);
        if (inst == null) return null;
        inst.setParent(parent);
        return inst;
    }

    /**
     @return Создает и возвращает новый GameObject
     */
    public GameObject instantiate(String path, Vector2 pos, Vector2 size, double angle){
        GameObject inst = getNewGameObject(path);
        if (inst == null) return null;
        inst.transform.position = pos;
        inst.transform.size = size;
        inst.transform.angle = angle;
        return inst;
    }

    /**
     * Устанавливает порядок отрисовки для текущего GameObject
     * @param viewOrder порядок отрисовки (меньшие значения - ниже, большие - выше)
     */
    public void setViewOrder(double viewOrder) {
        if (gameObject != null) {
            gameObject.setViewOrder(viewOrder);
        }
    }

    /**
     * Получает порядок отрисовки текущего GameObject
     */
    public double getViewOrder() {
        return gameObject != null ? gameObject.getViewOrder() : 0;
    }

    /**
     * Устанавливает порядок отрисовки для указанного GameObject
     */
    public void setViewOrder(GameObject obj, double viewOrder) {
        if (obj != null) {
            obj.setViewOrder(viewOrder);
        }
    }

    /**
     * Устанавливает порядок отрисовки для дочерних объектов
     */
    public void setChildsViewOrder(double baseViewOrder) {
        if (gameObject != null) {
            gameObject.setChildsViewOrder(baseViewOrder);
        }
    }

    /**
     * Создает GameObject с указанным порядком отрисовки
     */
    public GameObject instantiate(String path, Vector2 pos, Vector2 size, double angle, double viewOrder) {
        GameObject inst = instantiate(path, pos, size, angle);
        if (inst != null) {
            inst.setViewOrder(viewOrder);
        }
        return inst;
    }

    /**
     * Создает GameObject с указанным порядком отрисовки
     */
    public GameObject instantiate(String path, Vector2 pos, double viewOrder) {
        GameObject inst = instantiate(path, pos);
        if (inst != null) {
            inst.setViewOrder(viewOrder);
        }
        return inst;
    }

    /**
     * Создает GameObject с указанным порядком отрисовки
     */
    public GameObject instantiate(String path, double viewOrder) {
        GameObject inst = getNewGameObject(path);
        if (inst != null) {
            inst.setViewOrder(viewOrder);
        }
        return inst;
    }
    /**
     вызывается когда коллайдер объекта НАХОДИТСЯ В ЗОНЕ другого коллайдера
     */
    public void onCollisionStay(ArrayList<GameObject> collisions){}
    /**
     вызывается когда коллайдер объекта ВЫХОДИТ из соприкосновения с другим коллайдером
     */
    public void onCollisionExit(ArrayList<GameObject> collisions) {}

    /**
     вызывается когда коллайдер объекта СОПРИКАСАЕТСЯ с другим коллайдером
     */
    public void onCollisionEnter(ArrayList<GameObject> collisions) {}


    public GameObject instantiate(String path, Vector2 pos){
        GameObject inst = getNewGameObject(path);
        if (inst == null) return null;
        inst.transform.position = pos;
        return inst;
    }

    private GameObject getNewGameObject(String path){
        return GameObject.instantiate(path);
    }
}
