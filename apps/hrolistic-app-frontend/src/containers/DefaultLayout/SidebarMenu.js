import React, {Component, Suspense} from "react";
import {
    AppSidebarNav,
    AppSidebar,
    AppSidebarFooter,
    AppSidebarForm,
    AppSidebarHeader,
    AppSidebarMinimizer,
} from "@coreui/react";
import navigation from "../../_nav";
import {UserViews} from "../../_common"
import {connect} from "react-redux";

class SidebarMenu extends Component {

    constructor(props) {
        super(props);
        this.state = {
            navData: null,
        };
    }


    componentWillMount() {
        if (this.props.user)
        this.setState(UserViews(this.props.user));
    }


    render() {
        return (
            <AppSidebar fixed display="lg">
                <AppSidebarHeader />
                <AppSidebarForm />
                <Suspense>
                    <AppSidebarNav navConfig={(this.state.navData === null) ? navigation : this.state.navData} {...this.props} />
                </Suspense>
                <AppSidebarFooter />
                <AppSidebarMinimizer />
            </AppSidebar>
        )
    }
}
function mapState(state) {
    const {user} = state.authentication;
    return {user};
}

// export default  SidebarMenu;
export default  connect(mapState)(SidebarMenu);
