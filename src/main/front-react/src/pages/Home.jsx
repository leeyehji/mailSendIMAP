import React, {useState} from 'react';
import axios from "axios";

const Home = () => {
    const [data, setData] = useState([]);
    const [sendData, setSendData] = useState("");
    const testURL = "http://localhost:8080/test";

    const handleButtonClick = async() => {
        try{
            console.log(sendData)
            const response = await axios.post(testURL, sendData, {
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            });
            setData(response.data);
        }catch (error){
            console.log('Error fetching data: ', error);
        }
    }
    const handleDataInputBtn = () => {
        setSendData(sendData);
    }
    return (
        <div>
            <h1>홈화면입니다.</h1>
        </div>
    );
};

export default Home;