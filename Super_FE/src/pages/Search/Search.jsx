import React, { useState, useEffect } from "react";
import { SearchContainer, NavBar, NavBarRow, NavBarSelect, ResultGroup, ButtonReturn, ButtonOrder, ButtonResult } from "./NavBar";
import { useLocation, useNavigate } from "react-router-dom";

function Search() {
    const [orderSelected, setOrderSelected] = useState(false); // 오름차순/내림차순
    
    const navigate = useNavigate();
    const location = useLocation();
    const info = {...location.state};

    const handleReturnClick = () => {
        navigate("/First");
    };
    
    const handleOrderClick = () => {
        const buttonOrder = document.getElementById("order");
        if(buttonOrder.textContent === "평균 면적 크기 순 ↑") {
            buttonOrder.textContent = "평균 면적 크기 순 ↓";
            //정렬 순서 바꾸기
        } else {
            buttonOrder.textContent = "평균 면적 크기 순 ↑";
        }
        setOrderSelected(!orderSelected);  
    };

    const handleResultClick = ({item}) => {
        navigate("/SearchResult"  /*, {
            state: {
                navbarAddress: `${item.address}`,
                navBarSelect: `${item.select}`,
                navBarOrder: `${item.order}`,
            },
        } */);
    };

    return (
        <SearchContainer>
            <NavBar>
                <ButtonReturn
                onClick={handleReturnClick}
                >
                    ⌂
                </ButtonReturn>
                <NavBarRow>
                    <NavBarSelect
                    id = 'address'
                    >
                    <option>주소 입력</option>
                    </NavBarSelect>
                </NavBarRow>
                <NavBarRow>
                <NavBarSelect>
                    <option>조건 선택</option>
                    <option>가격</option>
                    <option>면적</option>
                </NavBarSelect>
                <NavBarSelect>
                    <option>조건 세분화</option>
                </NavBarSelect>
                <NavBarSelect>
                    <option>OO분 이내</option>
                </NavBarSelect>
                </NavBarRow>
                <NavBarRow>
                    <ButtonOrder
                    id = "order"
                    onClick = {handleOrderClick}
                    >
                        평균 면적 크기 순 ↓</ButtonOrder>
                </NavBarRow>
            </NavBar>
            <ResultGroup>
                <ButtonResult
                onClick = {handleResultClick}
                >
                    신내동</ButtonResult>
                <ButtonResult
                onClick = {handleResultClick}
                >
                    공릉동</ButtonResult>
                <ButtonResult
                onClick = {handleResultClick}
                >
                    장충동</ButtonResult>
                <ButtonResult
                onClick = {handleResultClick}
                >
                    필동</ButtonResult>
            </ResultGroup>
        </SearchContainer>
    );
}

export default Search;