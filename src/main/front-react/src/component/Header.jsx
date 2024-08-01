import React from 'react';
import {Link} from "react-router-dom";

const Header = () => {
    return (
        <div>
            <h1>헤더입니다.</h1>
            <ul>
                <li><Link to={"/"}>Home</Link></li>
                <li><Link to={"/MailSend"}>MailSend</Link></li>
            </ul>
            <hr/>
        </div>
    );
};

export default Header;