package com.team4.cardcase2.interfaces;
import com.team4.cardcase2.entity.Card;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.team4.cardcase2.entity.Element;
import com.team4.cardcase2.entity.User_card;
import com.team4.cardcase2.utils.JDBCutils;
public class CardDao {
    private Connection connection = null;
    private PreparedStatement pStmt = null;
    private ResultSet rs = null;

    public List<Card> getMyCards(int uid){
        Connection conn = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        List<Card> cards = new ArrayList<Card>();


        try {
            // 获取数据库连接
            conn = JDBCutils.getConnection();

            // 执行第一个查询,获取 uid 对应的所有 cid
            String sql1 = "SELECT card_id FROM user_card WHERE user_id = ?";
            stmt1 = conn.prepareStatement(sql1);
            stmt1.setInt(1, uid);
            rs1 = stmt1.executeQuery();

            // 遍历第一个查询结果,针对每个 cid 执行第二个查询
            while (rs1.next()) {
                int cid = rs1.getInt("card_id");
                String sql2 = "SELECT * FROM cards WHERE card_id = ?";
                stmt2 = conn.prepareStatement(sql2);
                stmt2.setInt(1, cid);
                rs2 = stmt2.executeQuery();
                Card card = new Card();
                List<Element> elements = new ArrayList<Element>();
                // 处理第二个查询结果,组装 Card 对象
                while (rs2.next()) {
                    Element element = new Element();
                    element.setElement_type(rs2.getString("element_type"));
                    element.setContent(rs2.getString("content"));
                    element.setPosition_x(rs2.getInt("position_x"));
                    element.setPosition_y(rs2.getInt("position_y"));
                    element.setStyle(rs2.getString("style"));
                    elements.add(element);
                }
                card.setCard_id(cid);
                card.setElements(elements);
                cards.add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            JDBCutils.release(rs2, stmt2, null);
            JDBCutils.release(rs1, stmt1, conn);
        }

        return cards;
    }

    public List<Card> getAllCards(){
        Connection conn = null;
        Statement stmt1 = null;
        PreparedStatement stmt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        List<Card> cards = new ArrayList<Card>();
        try {
            // 获取数据库连接
            conn = JDBCutils.getConnection();

            // 执行第一个查询,获取 uid 对应的所有 cid
            String sql1 = "SELECT card_id FROM user_card;";
            stmt1 = conn.createStatement();
            rs1 = stmt1.executeQuery(sql1);

            // 遍历第一个查询结果,针对每个 cid 执行第二个查询
            while (rs1.next()) {
                int cid = rs1.getInt("card_id");
                String sql2 = "SELECT * FROM cards WHERE card_id = ?";
                stmt2 = conn.prepareStatement(sql2);
                stmt2.setInt(1, cid);
                rs2 = stmt2.executeQuery();
                Card card = new Card();
                List<Element> elements = new ArrayList<Element>();
                // 处理第二个查询结果,组装 Card 对象
                while (rs2.next()) {
                    Element element = new Element();
                    element.setElement_type(rs2.getString("element_type"));
                    element.setContent(rs2.getString("content"));
                    element.setPosition_x(rs2.getInt("position_x"));
                    element.setPosition_y(rs2.getInt("position_y"));
                    element.setStyle(rs2.getString("style"));
                    elements.add(element);
                }
                card.setCard_id(cid);
                card.setElements(elements);
                cards.add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            JDBCutils.release(rs2, stmt2, null);
            JDBCutils.release(rs1, stmt1, conn);
        }

        return cards;
    }
    public List<Card> getCardByType(int type){

        return null;
    }
    public Card getCardById(int cid){
        Connection conn = null;
        PreparedStatement stmt2 = null;
        ResultSet rs2 = null;
        Card card = new Card();
        try {
            // 获取数据库连接
            conn = JDBCutils.getConnection();

            String sql2 = "SELECT * FROM cards WHERE card_id = ?";
            stmt2 = conn.prepareStatement(sql2);
            stmt2.setInt(1, cid);
            rs2 = stmt2.executeQuery();

            List<Element> elements = new ArrayList<Element>();
            // 处理第二个查询结果,组装 Card 对象
            while (rs2.next()) {
                Element element = new Element();
                element.setElement_type(rs2.getString("element_type"));
                element.setContent(rs2.getString("content"));
                element.setPosition_x(rs2.getInt("position_x"));
                element.setPosition_y(rs2.getInt("position_y"));
                element.setStyle(rs2.getString("style"));
                elements.add(element);
            }
            card.setCard_id(cid);
            card.setElements(elements);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            JDBCutils.release(rs2, stmt2, conn);
        }

        return card;
    }

    public boolean insertCard(Card card, User_card user_card){
        Connection conn = null;
        PreparedStatement stmt = null;
        List<Element> elements = card.getElements();
        try {
            // 获取数据库连接
            conn = JDBCutils.getConnection();
            // 开启事务
            conn.setAutoCommit(false);
            // 准备 SQL 语句
            String sql = "INSERT INTO user_card (card_id, user_id, avatar, background, design) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, card.getCard_id());
            stmt.setInt(2, user_card.getUser_id());
            stmt.setBytes(3, user_card.getAvatar());
            stmt.setBytes(4, user_card.getBackground());
            stmt.setString(5, user_card.getDesign());
            stmt.executeUpdate();
            sql = "INSERT INTO cards (card_id, element_type, content, position_x, position_y, style) VALUES (?, ?, ?, ?, ?, ?)";
            for (Element element : elements) {
                stmt.setInt(1, card.getCard_id());
                stmt.setString(2, element.getElement_type());
                stmt.setString(3, element.getContent());
                stmt.setInt(4, element.getPosition_x());
                stmt.setInt(5, element.getPosition_y());
                stmt.setString(6, element.getStyle());
                stmt.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            JDBCutils.release(null, stmt, conn);
        }
        return false;
    }
    public boolean deleteCard(Card card){
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            //获得数据的连接
            conn = JDBCutils.getConnection();
            //获得Statement对象
            String sql1 = "DELETE FROM user_card WHERE card_id = ?";
            String sql2 = "DELETE FROM cards WHERE card_id = ?";
            stmt = conn.prepareStatement(sql1);
            stmt.setInt(1, card.getCard_id());
            int affect_row1 = stmt.executeUpdate();

            stmt = conn.prepareStatement(sql2);
            stmt.setInt(1, card.getCard_id());
            int affect_row2 = stmt.executeUpdate();

            return affect_row1>0 && affect_row2>0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(null, stmt, conn);
        }
        return false;
    }
    public boolean updateCard(Card card){
        Connection conn = null;
        PreparedStatement stmt = null;
        List<Element> elements = card.getElements();
        int num = 0;
        try {
            //获得数据的连接
            conn = JDBCutils.getConnection();
            //获得Statement对象

            for (Element element : elements){
                String sql = "update cards set content = ?, position_x = ?, position_y = ?, style = ? where card_id = ?, element_type = ?";

                stmt = conn.prepareStatement(sql);
                stmt.setString(1, element.getContent());
                stmt.setInt(2, element.getPosition_x());
                stmt.setInt(3, element.getPosition_y());
                stmt.setString(4, element.getStyle());
                stmt.setInt(5, card.getCard_id());
                stmt.setString(6, element.getElement_type());
                num += stmt.executeUpdate();
            }

            return num > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCutils.release(null, stmt, conn);
        }
        return false;
    }
}
