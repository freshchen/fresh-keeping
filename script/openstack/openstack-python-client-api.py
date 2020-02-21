from keystoneauth1.identity import v3
from keystoneauth1 import session
from keystoneauth1 import loading
from keystoneclient.v3 import client as keyclient
from glanceclient import Client
from novaclient import client
from neutronclient.v2_0 import client as ntclient
from heatclient import client as hclient


def get_keystone_session():
    loader = loading.get_plugin_loader('password')
    auth = v3.Password(auth_url="http://<Openstack Controller Ip>:5000/v3", username="admin", password="<pwd>",
                       project_name="admin", user_domain_id="default", project_domain_id="default")
    sess = session.Session(auth=auth)
    return sess


def get_nova_client():
    sess = get_keystone_session()
    nova = client.Client(2, session=sess)
    return nova


def get_glance_client():
    sess = get_keystone_session()
    glance = Client('2', session=sess)
    return glance


def get_keystone_client():
    sess = get_keystone_session()
    keystone = keyclient.Client(session=sess)
    return keystone


def get_neutron_client():
    sess = get_keystone_session()
    neutron = ntclient.Client(session=sess)
    return neutron


def get_heat_client():
    creds = {}
    creds['username'] = 'admin'
    creds['password'] = 'admin1234'
    creds['auth_url'] = 'http://<Openstack Controller Ip>:5000/v3'
    creds['project_name'] = 'admin'
    ks_client = keyclient.Client(**creds)
    heat_endpoint = ks_client.service_catalog.url_for(service_type='orchestration', endpoint_type='publicURL')
    heat = hclient.Client('1', heat_endpoint, token=ks_client.auth_token)
    return heat


def list_images():
    glance = get_glance_client()
    list = glance.images.list()
    return list


def show_images():
    list = list_images()
    for image in list:
        print
        image.name, image.id, image.status


def get_image_id_by_name(image_name):
    list = list_images()
    id = ''
    for image in list:
        if image.name == image_name:
            id = image.id
            return id
    return id


def upload_image(image_name, image_path):
    glance = get_glance_client()
    glance.images.create(name=image_name, disk_format="qcow2", container_format="bare", is_public="true")
    id = get_image_id_by_name(image_name)
    glance.images.upload(id, open(image_path, 'rb'))


def delete_image(image_name):
    glance = get_glance_client()
    id = get_image_id_by_name(image_name)
    if id != '':
        glance.images.delete(id)
